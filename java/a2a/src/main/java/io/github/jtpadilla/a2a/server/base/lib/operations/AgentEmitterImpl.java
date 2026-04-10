package io.github.jtpadilla.a2a.server.base.lib.operations;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.A2AError;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.AgentEmitter;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.RequestContext;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
 *   <li><b>Eventos personalizados:</b> {@link #taskBuilder()}, {@link #messageBuilder()}, {@link #addTask(Task)}, {@link #emitEvent(Event)}</li>
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
 * @see io.a2a.server.agentexecution.AgentExecutor
 * @see RequestContext
 * @see EventQueue
 * @since 1.0.0
 */
public class AgentEmitterImpl implements AgentEmitter {
    private final EventQueue eventQueue;
    private final String taskId;
    private final String contextId;
    private final AtomicBoolean terminalStateReached = new AtomicBoolean(false);

    /**
     * Crea un nuevo AgentEmitter para el contexto de solicitud y la cola de eventos dados.
     *
     * @param context el contexto de solicitud que contiene los IDs de tarea y contexto
     * @param eventQueue la cola de eventos para encolar eventos
     */
    public AgentEmitterImpl(RequestContext context, EventQueue eventQueue) {
        this.eventQueue = eventQueue;
        this.taskId = context.getTaskId();
        this.contextId = context.getContextId();
    }

    private void updateStatus(TaskState taskState) {
        updateStatus(taskState, null, taskState.isFinal());
    }

    /**
     * Actualiza el estado de la tarea al estado indicado con un mensaje opcional.
     *
     * @param taskState el nuevo estado de la tarea
     * @param message mensaje opcional a incluir con la actualización de estado
     */
    public void updateStatus(TaskState taskState, @Nullable Message message) {
        updateStatus(taskState, message, taskState.isFinal());
    }

    /**
     * Actualiza el estado de la tarea al estado indicado con un mensaje opcional e indicador de finalidad.
     *
     * @param state el nuevo estado de la tarea
     * @param message mensaje opcional a incluir con la actualización de estado
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    private void updateStatus(TaskState state, @Nullable Message message, boolean isFinal) {
        // Comprobar estado terminal primero (fallo rápido)
        if (terminalStateReached.get()) {
            throw new IllegalStateException("Cannot update task status - terminal state already reached");
        }

        // Para estados finales, establecer el indicador de forma atómica
        if (isFinal) {
            if (!terminalStateReached.compareAndSet(false, true)) {
                throw new IllegalStateException("Cannot update task status - terminal state already reached");
            }
        }

        TaskStatusUpdateEvent event = TaskStatusUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .status(new TaskStatus(state, message, null))
                .build();
        eventQueue.enqueueEvent(event);
    }

    /**
     * Devuelve el ID de contexto de este emitter.
     *
     * @return el ID de contexto, o null si no está disponible
     */
    public @Nullable String getContextId() {
        return this.contextId;
    }

    /**
     * Devuelve el ID de tarea de este emitter.
     *
     * @return el ID de tarea, o null si no hay tarea asociada
     */
    public @Nullable String getTaskId() {
        return this.taskId;
    }

    /**
     * Añade un artefacto con las partes indicadas a la tarea.
     *
     * @param parts las partes a incluir en el artefacto
     */
    public void addArtifact(List<Part<?>> parts) {
        addArtifact(parts, null, null, null);
    }

    /**
     * Añade un artefacto con las partes, ID de artefacto, nombre y metadatos indicados.
     *
     * @param parts las partes a incluir en el artefacto
     * @param artifactId ID de artefacto opcional (se genera si es null)
     * @param name nombre opcional del artefacto
     * @param metadata mapa de metadatos opcional
     */
    public void addArtifact(List<Part> parts, @Nullable String artifactId, @Nullable String name, @Nullable Map<String, Object> metadata) {
        addArtifact(parts, artifactId, name, metadata, null, null);
    }

    /**
     * Añade un artefacto con todos los parámetros opcionales.
     *
     * @param parts las partes a incluir en el artefacto
     * @param artifactId ID de artefacto opcional (se genera si es null)
     * @param name nombre opcional del artefacto
     * @param metadata mapa de metadatos opcional
     * @param append si se debe añadir al final de un artefacto existente
     * @param lastChunk si este es el último fragmento de una secuencia en streaming
     */
    public void addArtifact(List<Part> parts, @Nullable String artifactId, @Nullable String name, @Nullable Map<String, Object> metadata,
                            @Nullable Boolean append, @Nullable Boolean lastChunk) {
        if (artifactId == null) {
            artifactId = UUID.randomUUID().toString();
        }
        TaskArtifactUpdateEvent event = TaskArtifactUpdateEvent.newBuilder()
                .setTaskId(taskId)
                .setContextId(contextId)
                .setArtifact(
                        Artifact.newBuilder()
                                .setArtifactId(artifactId)
                                .setName(name)
                                .addAllParts(parts)
                                .metadata(metadata)
                                .build()
                )
                .append(append)
                .lastChunk(lastChunk)
                .build();
        eventQueue.enqueueEvent(event);
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
    public Message newAgentMessage(List<Part<?>> parts, @Nullable Map<String, Object> metadata) {
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
     * Envía un mensaje de texto simple al cliente.
     * Método de conveniencia para agentes que responden con texto plano sin crear una tarea.
     *
     * @param text el contenido de texto a enviar
     */
    public void sendMessage(String text) {
        sendMessage(List.of(new TextPart(text)));
    }

    /**
     * Envía un mensaje con partes personalizadas (texto, imágenes, etc.) al cliente.
     * Úsalo para respuestas enriquecidas que no requieren gestión del ciclo de vida de la tarea.
     *
     * @param parts las partes del mensaje a enviar
     */
    public void sendMessage(List<Part<?>> parts) {
        sendMessage(parts, null);
    }

    /**
     * Envía un mensaje con partes y metadatos al cliente.
     * Crea un mensaje de agente con los IDs de tarea y contexto actuales (si están disponibles)
     * y lo encola en la cola de eventos.
     *
     * @param parts las partes del mensaje a enviar
     * @param metadata metadatos opcionales a adjuntar al mensaje
     */
    public void sendMessage(List<Part<?>> parts, @Nullable Map<String, Object> metadata) {
        Message message = newAgentMessage(parts, metadata);
        eventQueue.enqueueEvent(message);
    }

    /**
     * Envía un objeto Message existente directamente al cliente.
     * <p>
     * Úsalo cuando necesites reenviar o hacer eco de un mensaje existente sin crear uno nuevo.
     * El mensaje se encola tal cual, preservando su messageId, metadatos y todos los demás campos.
     * </p>
     * <p>
     * <b>Nota:</b> Se usa típicamente para reenviar mensajes de usuario o preservar propiedades
     * específicas del mensaje. En la mayoría de los casos, prefiere {@link #sendMessage(String)} o
     * {@link #sendMessage(List)}, que crean nuevos mensajes de agente con IDs generados.
     * </p>
     * <p>Ejemplo de uso:
     * <pre>{@code
     * public void execute(RequestContext context, AgentEmitter emitter) {
     *     // Devuelve el mensaje del usuario como eco
     *     emitter.sendMessage(context.getMessage());
     * }
     * }</pre>
     *
     * @param message el mensaje a enviar al cliente
     * @since 1.0.0
     */
    public void sendMessage(Message message) {
        eventQueue.enqueueEvent(message);
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
     * Emite un objeto Event personalizado al cliente.
     * <p>
     * Es un método de propósito general para emitir cualquier tipo de Event. La mayoría de los
     * agentes deberían usar los métodos de conveniencia ({@link #sendMessage(String)},
     * {@link #addTask(Task)}, {@link #addArtifact(List)}, {@link #complete()}, etc.), pero este
     * método ofrece flexibilidad para agentes que necesitan crear y emitir eventos personalizados
     * usando los builders de eventos.
     * </p>
     * <p>Ejemplo de uso:
     * <pre>{@code
     * public void execute(RequestContext context, AgentEmitter emitter) {
     *     // Crear un TaskStatusUpdateEvent personalizado
     *     TaskStatusUpdateEvent event = TaskStatusUpdateEvent.builder()
     *         .taskId(context.getTaskId())
     *         .contextId(context.getContextId())
     *         .status(new TaskStatus(TaskState.WORKING))
     *         .isFinal(false)
     *         .build();
     *     emitter.emitEvent(event);
     * }
     * }</pre>
     *
     * @param event el evento a emitir
     * @since 1.0.0
     */
    public void emitEvent(Event event) {
        eventQueue.enqueueEvent(event);
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
    public Message.Builder messageBuilder() {
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
