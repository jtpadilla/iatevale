package io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl;

import com.google.lf.a2a.v1.*;
import com.google.protobuf.Struct;
import io.github.jtpadilla.a2a.server.base.lib.model.TaskStateUtil;
import io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl.event.EmitterEvent;
import io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl.event.EmitterMessageEvent;
import io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl.event.EmitterTaskArtifactUpdateEvent;
import io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl.event.EmitterTaskStatusUpdateEvent;
import io.github.jtpadilla.a2a.server.base.lib.spec.A2AError;
import io.github.jtpadilla.a2a.server.base.lib.spec.Emitter;
import io.github.jtpadilla.a2a.server.base.lib.spec.EmitterException;
import io.github.jtpadilla.a2a.server.base.lib.spec.RequestContext;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Auxiliar para emitir eventos desde implementaciones de AgentExecutor.
 *
 * <p>AgentEmitter proporciona una API simplificada para que los agentes se comuniquen con los
 * clientes a través del protocolo A2A. Gestiona tanto el ciclo de vida de las tareas como el
 * envío directo de mensajes, poblando automáticamente los eventos con los IDs correctos de
 * tarea y contexto del RequestContext.
 *
 * <h2>Capacidades principales</h2>
 * <ul>
 *   <li><b>Ciclo de vida de la tarea:</b> {@link #submit()}, {@link #startWork()}, {@link #complete()},
 *       {@link #fail()}, {@link #cancel()}, {@link #reject()}</li>
 *   <li><b>Envío de mensajes:</b> {@link #sendMessage(String)}, {@link #sendMessage(List)},
 *       {@link #sendMessage(List, Map)}</li>
 *   <li><b>Streaming de artefactos:</b> {@link #addArtifact(List)}, {@link #addArtifact(List, String, String, Map)}</li>
 *   <li><b>Requisitos de auth/entrada:</b> {@link #requiresAuth()}, {@link #requiresInput()}</li>
 *   <li><b>Eventos personalizados:</b> {@link #taskBuilder()}, {@link #messageBuilder()}, {@link #addTask(Task)}</li>
 * </ul>
 *
 * <h2>Patrones de uso</h2>
 *
 * <h3>Respuesta simple con mensaje (sin tarea)</h3>
 * <pre>{@code
 * public void execute(RequestContext context, AgentEmitter emitter) {
 *     String response = processRequest(context.getUserInput("\n"));
 *     emitter.sendMessage(response);
 * }
 * }</pre>
 *
 * <h3>Ciclo de vida de tarea con artefactos</h3>
 * <pre>{@code
 * public void execute(RequestContext context, AgentEmitter emitter) {
 *     if (context.getTask() == null) {
 *         emitter.submit();  // Crea la tarea en estado SUBMITTED
 *     }
 *     emitter.startWork();  // Transición a WORKING
 *
 *     // Procesar y transmitir resultados
 *     List<Part<?>> results = doWork(context.getUserInput("\n"));
 *     emitter.addArtifact(results);
 *
 *     emitter.complete();  // Marcar como COMPLETED
 * }
 * }</pre>
 *
 * <h3>Respuesta en streaming (LLM)</h3>
 * <pre>{@code
 * public void execute(RequestContext context, AgentEmitter emitter) {
 *     emitter.startWork();
 *
 *     for (String chunk : llmService.stream(context.getUserInput("\n"))) {
 *         emitter.addArtifact(List.of(new TextPart(chunk)));
 *     }
 *
 *     emitter.complete();
 * }
 * }</pre>
 *
 * <h2>Gestión de IDs de evento</h2>
 * Todos los eventos emitidos se pueblan automáticamente con:
 * <ul>
 *   <li><b>taskId:</b> Del RequestContext (puede ser null para respuestas solo con mensaje)</li>
 *   <li><b>contextId:</b> Del RequestContext</li>
 *   <li><b>messageId:</b> UUID generado para mensajes</li>
 *   <li><b>artifactId:</b> UUID generado para artefactos (salvo que se indique explícitamente)</li>
 * </ul>
 *
 * Los eventos son validados por el EventQueue para garantizar la corrección del taskId.
 *
 */
public class AgentEmitterImpl implements Emitter {

    private final Consumer<EmitterEvent> emitter;
    private final String taskId;
    private final String contextId;
    private final AtomicBoolean terminalStateReached = new AtomicBoolean(false);

    /**
     * Crea un nuevo AgentEmitter para el contexto de solicitud y la cola de eventos dados.
     *
     * @param context el contexto de solicitud que contiene los IDs de tarea y contexto
     * @param emitter mecanismo para enviar eventos
     */
    public AgentEmitterImpl(RequestContext context, Consumer<EmitterEvent> emitter) {
        this.emitter = emitter;
        this.taskId = context.getTaskId();
        this.contextId = context.getContextId();
    }

    public Optional<String> taskId() {
        return Optional.ofNullable(taskId);
    }

    public Optional<String> contextId() {
        return Optional.ofNullable(contextId);
    }

    private String taskIdRequired() throws EmitterException {
        return taskId().orElseThrow(()->new EmitterException("TaskId is missing"));
    }

    private String contextIdRequired() throws EmitterException {
        return contextId().orElseThrow(()->new EmitterException("ContextId is missing"));
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Se envia un mensaje como respuesta sin crear una tarea
    /////////////////////////////////////////////////////////////////////////////////

    @Override
    public Message.Builder messageBuilder() {
        return Message.newBuilder()
                .setRole(Role.ROLE_AGENT)
                .setMessageId(UUID.randomUUID().toString());
    }

    @Override
    public void messageSend(Message message) throws EmitterException {
        // Se encola el evento
        emitter.accept(new EmitterMessageEvent(message));
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Se le envia al cliente la actualizacion del estado de una tarea
    // ya existente
    /////////////////////////////////////////////////////////////////////////////////

    @Override
    public void taskStatusUpdate(TaskStatus taskStatus) throws EmitterException {
        taskStatusUpdateImpl(taskStatus, null);
    }

    @Override
    public void taskStatusUpdate(TaskStatus taskStatus, Struct metadata) throws EmitterException {
        taskStatusUpdateImpl(taskStatus, metadata);
    }

    private void taskStatusUpdateImpl(TaskStatus taskStatus, @Nullable Struct metadata) throws EmitterException {

        final boolean isTerminal = TaskStateUtil.isTerminal(taskStatus.getState());

        // Comprobar estado terminal primero (fallo rápido)
        if (terminalStateReached.get()) {
            throw new IllegalStateException("Cannot update task status - terminal state already reached");
        }

        // Para estados finales, establecer el indicador de forma atómica
        if (isTerminal) {
            if (!terminalStateReached.compareAndSet(false, true)) {
                throw new IllegalStateException("Cannot update task status - terminal state already reached");
            }
        }

        // Se compone el mensaje dirigido al cliente
        final TaskStatusUpdateEvent.Builder taskStatusUpdateEvent = TaskStatusUpdateEvent.newBuilder()
                .setTaskId(taskIdRequired())
                .setContextId(contextIdRequired())
                .setStatus(taskStatus);
        if (metadata != null) {
            taskStatusUpdateEvent.setMetadata(metadata);
        }

        // Se encola el evento
        emitter.accept(new EmitterTaskStatusUpdateEvent(taskStatusUpdateEvent.build()));

    }

    /////////////////////////////////////////////////////////////////////////////////
    // Se le envia al cliente el delta de un Artifact que se ha generado en una tarea
    // ya existente (puede ser un artifact completo).
    /////////////////////////////////////////////////////////////////////////////////

    @Override
    public void taskArtifactUpdate(Artifact artifact) throws EmitterException {
        taskArtifactUpdateImpl(artifact, false, true, null);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, Struct metadata) throws EmitterException {
        taskArtifactUpdateImpl(artifact, false, true, metadata);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk) throws EmitterException {
        taskArtifactUpdateImpl(artifact, append, lastChunk, null);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk, Struct metadata) throws EmitterException {
        taskArtifactUpdateImpl(artifact, append, lastChunk, metadata);
    }

    private void taskArtifactUpdateImpl(Artifact artifact, boolean append, boolean lastChunk, @Nullable Struct metadata) throws EmitterException {

        // Se compone el mensaje dirigido al cliente
        final TaskArtifactUpdateEvent.Builder taskArtifactUpdateEvent = TaskArtifactUpdateEvent.newBuilder()
                .setTaskId(taskIdRequired())
                .setContextId(contextIdRequired())
                .setArtifact(artifact)
                .setAppend(append)
                .setLastChunk(lastChunk);
        if (metadata != null) {
            taskArtifactUpdateEvent.setMetadata(metadata);
        }

        // Se encola el evento
        emitter.accept(new EmitterTaskArtifactUpdateEvent(taskArtifactUpdateEvent.build()));

    }





    /**
     * Marca la tarea como COMPLETED.
     */
    public void complete() {
        complete(null);
    }

    /**
     * Marca la tarea como COMPLETED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir con la finalización
     */
    public void complete(@Nullable Message message) {
        updateStatus(TaskState.TASK_STATE_COMPLETED, message);
    }

    /**
     * Marca la tarea como FAILED.
     */
    public void fail() {
        fail((Message) null);
    }

    /**
     * Marca la tarea como FAILED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir con el fallo
     */
    public void fail(@Nullable Message message) {
        updateStatus(TaskState.TASK_STATE_FAILED, message);
    }

    /**
     * Encola un evento de error A2A que transicionará automáticamente la tarea a FAILED.
     * <p>
     * Úsalo cuando necesites fallar la tarea con un error A2A específico (como
     * {@link io.a2a.spec.UnsupportedOperationError}, {@link io.a2a.spec.InvalidRequestError},
     * {@link io.a2a.spec.TaskNotFoundError}, etc.) que deba enviarse al cliente.
     * </p>
     * <p>
     * El evento de error se encola y el MainEventBusProcessor transicionará automáticamente
     * la tarea al estado FAILED. Esto garantiza transiciones de estado seguras para hilos sin
     * condiciones de carrera, ya que el MainEventBusProcessor monohilo gestiona todas las
     * actualizaciones de estado.
     * </p>
     * <p>
     * Los eventos de error son terminales (detienen el consumo de eventos) y desencadenan la
     * transición automática al estado FAILED. Los detalles del error se envían únicamente al
     * cliente originador, mientras que el estado FAILED se replica en todos los nodos en
     * despliegues multi-instancia.
     * </p>
     * <p>Ejemplo de uso:
     * <pre>{@code
     * public void execute(RequestContext context, AgentEmitter emitter) {
     *     if (!isSupported(context.getMessage())) {
     *         emitter.fail(new UnsupportedOperationError("Feature not supported"));
     *         return;
     *     }
     *     // ... procesamiento normal
     * }
     * }</pre>
     *
     * @param error el error A2A a encolar y enviar al cliente
     * @since 1.0.0
     */
    public void fail(A2AError error) {
        // Establecer el indicador de estado terminal ANTES de encolar el error.
        // Evita condiciones de carrera donde el agente llama a fail(error) y luego a complete()
        if (!terminalStateReached.compareAndSet(false, true)) {
            throw new IllegalStateException("Cannot update task status - terminal state already reached");
        }

        eventQueue.enqueueEvent(error);
        // La transición de estado ocurre automáticamente en MainEventBusProcessor.
        // El evento de error es terminal y desencadenará la transición al estado FAILED.
    }

    /**
     * Marca la tarea como SUBMITTED.
     */
    public void submit() {
        submit(null);
    }

    /**
     * Marca la tarea como SUBMITTED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    public void submit(@Nullable Message message) {
        updateStatus(TaskState.TASK_STATE_SUBMITTED, message);
    }

    /**
     * Marca la tarea como WORKING (en proceso activo).
     */
    public void startWork() {
        startWork(null);
    }

    /**
     * Marca la tarea como WORKING con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    public void startWork(@Nullable Message message) {
        updateStatus(TaskState.TASK_STATE_WORKING, message);
    }

    /**
     * Marca la tarea como CANCELED.
     */
    public void cancel() {
        cancel(null);
    }

    /**
     * Marca la tarea como CANCELED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    public void cancel(@Nullable Message message) {
        updateStatus(TaskState.TASK_STATE_CANCELED, message);
    }

    /**
     * Marca la tarea como REJECTED.
     */
    public void reject() {
        reject(null);
    }

    /**
     * Marca la tarea como REJECTED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    public void reject(@Nullable Message message) {
        updateStatus(TaskState.TASK_STATE_REJECTED, message);
    }

    /**
     * Marca la tarea como INPUT_REQUIRED, indicando que el agente necesita entrada del usuario para continuar.
     */
    public void requiresInput() {
        requiresInput(null, false);
    }

    /**
     * Marca la tarea como INPUT_REQUIRED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    public void requiresInput(@Nullable Message message) {
        requiresInput(message, false);
    }

    /**
     * Marca la tarea como INPUT_REQUIRED con un indicador de finalidad.
     *
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    public void requiresInput(boolean isFinal) {
        requiresInput(null, isFinal);
    }

    /**
     * Marca la tarea como INPUT_REQUIRED con un mensaje opcional e indicador de finalidad.
     *
     * @param message mensaje opcional a incluir
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    public void requiresInput(@Nullable Message message, boolean isFinal) {
        updateStatus(TaskState.TASK_STATE_INPUT_REQUIRED, message, isFinal);
    }

    /**
     * Marca la tarea como AUTH_REQUIRED, indicando que el agente necesita autenticación para continuar.
     */
    public void requiresAuth() {
        requiresAuth(null, false);
    }

    /**
     * Marca la tarea como AUTH_REQUIRED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    public void requiresAuth(@Nullable Message message) {
        requiresAuth(message, false);
    }

    /**
     * Marca la tarea como AUTH_REQUIRED con un indicador de finalidad.
     *
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    public void requiresAuth(boolean isFinal) {
        requiresAuth(null, isFinal);
    }

    /**
     * Marca la tarea como AUTH_REQUIRED con un mensaje opcional e indicador de finalidad.
     *
     * @param message mensaje opcional a incluir
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    public void requiresAuth(@Nullable Message message, boolean isFinal) {
        updateStatus(TaskState.TASK_STATE_AUTH_REQUIRED, message, isFinal);
    }

    /**
     * Crea un nuevo mensaje de agente con las partes y metadatos indicados.
     * Puebla el mensaje con el rol de agente, el ID de tarea, el ID de contexto y un ID de mensaje generado.
     *
     * @param parts las partes a incluir en el mensaje
     * @param metadata metadatos opcionales a adjuntar al mensaje
     * @return un nuevo objeto Message listo para enviar
     */
    public Message newAgentMessage(List<Part> parts, @Nullable Map<String, Object> metadata) {
        return Message.builder()
                .role(Message.Role.ROLE_AGENT)
                .taskId(taskId)
                .contextId(contextId)
                .messageId(UUID.randomUUID().toString())
                .metadata(metadata)
                .parts(parts)
                .build();
    }

    /**
     * Añade un objeto Task personalizado para enviarlo al cliente.
     * <p>
     * Úsalo cuando necesites crear una Task con campos específicos (historial, artefactos, etc.)
     * que los métodos de conveniencia como {@link #submit()}, {@link #startWork()} o
     * {@link #complete()} no proporcionan.
     * </p>
     * <p>
     * <b>Patrón de uso habitual:</b> Construye una tarea con {@link #taskBuilder()}, personalízala
     * y añádela con este método.
     * </p>
     * <p>Ejemplo de uso:
     * <pre>{@code
     * public void execute(RequestContext context, AgentEmitter emitter) {
     *     // Crear una tarea con estado e historial específicos
     *     Task task = emitter.taskBuilder()
     *         .status(new TaskStatus(TaskState.SUBMITTED))
     *         .history(List.of(context.getMessage()))
     *         .build();
     *     emitter.addTask(task);
     * }
     * }</pre>
     *
     * @param task la tarea a añadir
     * @since 1.0.0
     */
    public void addTask(Task task) {
        eventQueue.enqueueEvent(task);
    }


    /**
     * Crea un Task.Builder pre-poblado con los IDs de tarea y contexto correctos.
     * Los agentes pueden personalizar otros campos de Task (estado, artefactos, etc.) antes de llamar a build().
     *
     * <p>Ejemplo de uso:
     * <pre>{@code
     * Task task = emitter.taskBuilder()
     *     .status(new TaskStatus(TaskState.WORKING))
     *     .build();
     * }</pre>
     *
     * @return un Task.Builder con id y contextId ya establecidos
     */
    public Task.Builder taskBuilder() {
        return Task.builder()
                .id(taskId)
                .contextId(contextId);
    }

    /**
     * Crea un Message.Builder pre-poblado con los valores por defecto del agente.
     * Establece taskId solo si no es null (los mensajes pueden existir independientemente de las tareas).
     *
     * <p>Campos pre-poblados:
     * <ul>
     *   <li>taskId - establecido solo si este AgentEmitter tiene un taskId no nulo</li>
     *   <li>contextId - ID de contexto actual</li>
     *   <li>role - Message.Role.AGENT</li>
     *   <li>messageId - UUID generado</li>
     * </ul>
     *
     * <p>Ejemplo de uso:
     * <pre>{@code
     * Message msg = emitter.messageBuilder()
     *     .parts(List.of(new TextPart("Hola")))
     *     .metadata(Map.of("key", "value"))
     *     .build();
     * }</pre>
     *
     * @return un Message.Builder con los campos comunes del agente ya establecidos
     */
    public Message.Builder messageBuilderXXX() {
        Message.Builder builder = Message.builder()
                .contextId(contextId)
                .role(Message.Role.ROLE_AGENT)
                .messageId(UUID.randomUUID().toString());

        // Establecer taskId solo si está presente (los mensajes pueden existir sin tareas)
        if (taskId != null) {
            builder.taskId(taskId);
        }

        return builder;
    }

}
