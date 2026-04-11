package io.github.jtpadilla.a2a.server.base.lib.spec;

import com.google.lf.a2a.v1.*;
import com.google.protobuf.Struct;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 * @see RequestContext
 * @since 1.0.0
 */
public interface Emitter {

    /**
     * Actualiza el estado de la tarea al estado indicado con un mensaje opcional.
     *
     * @param taskState el nuevo estado de la tarea
     * @param message mensaje opcional a incluir con la actualización de estado
     */
    void updateStatus(TaskState taskState, @Nullable Message message) throws EmitterException;

    /**
     * Devuelve el ID de contexto de este emitter.
     *
     * @return el ID de contexto, o null si no está disponible
     */
    Optional<String> contextId();

    /**
     * Devuelve el ID de tarea de este emitter.
     *
     * @return el ID de tarea, o null si no hay tarea asociada
     */
    Optional<String> taskId();

    // Enviar mensajes si tener una tarea creada
    Message.Builder messageBuilder();

    // Actualizar el estado de la tarea
    void taskStatusUpdate(TaskStatus taskStatus) throws EmitterException;
    void taskStatusUpdate(TaskStatus taskStatus, Struct metadata) throws EmitterException;

    // Actualizar el artefacto de la tarea
    void taskArtifactUpdate(Artifact artifact) throws EmitterException;
    void taskArtifactUpdate(Artifact artifact, Struct metadata) throws EmitterException;
    void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk) throws EmitterException;
    void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk, Struct metadata) throws EmitterException;





    /**
     * Marca la tarea como COMPLETED.
     */
    void complete();

    /**
     * Marca la tarea como COMPLETED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir con la finalización
     */
    void complete(@Nullable Message message);

    /**
     * Marca la tarea como FAILED.
     */
    void fail();

    /**
     * Marca la tarea como FAILED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir con el fallo
     */
    void fail(@Nullable Message message);

    /**
     * Encola un evento de error A2A que transicionará automáticamente la tarea a FAILED.
     *
     * @param error el error A2A a encolar y enviar al cliente
     * @since 1.0.0
     */
    void fail(A2AError error);

    /**
     * Marca la tarea como SUBMITTED.
     */
    void submit();

    /**
     * Marca la tarea como SUBMITTED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    void submit(@Nullable Message message);

    /**
     * Marca la tarea como WORKING (en proceso activo).
     */
    void startWork();

    /**
     * Marca la tarea como WORKING con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    void startWork(@Nullable Message message);

    /**
     * Marca la tarea como CANCELED.
     */
    void cancel();

    /**
     * Marca la tarea como CANCELED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    void cancel(@Nullable Message message);

    /**
     * Marca la tarea como REJECTED.
     */
    void reject();

    /**
     * Marca la tarea como REJECTED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    void reject(@Nullable Message message);

    /**
     * Marca la tarea como INPUT_REQUIRED.
     */
    void requiresInput();

    /**
     * Marca la tarea como INPUT_REQUIRED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    void requiresInput(@Nullable Message message);

    /**
     * Marca la tarea como INPUT_REQUIRED con un indicador de finalidad.
     *
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    void requiresInput(boolean isFinal);

    /**
     * Marca la tarea como INPUT_REQUIRED con un mensaje opcional e indicador de finalidad.
     *
     * @param message mensaje opcional a incluir
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    void requiresInput(@Nullable Message message, boolean isFinal);

    /**
     * Marca la tarea como AUTH_REQUIRED.
     */
    void requiresAuth();

    /**
     * Marca la tarea como AUTH_REQUIRED con un mensaje opcional.
     *
     * @param message mensaje opcional a incluir
     */
    void requiresAuth(@Nullable Message message);

    /**
     * Marca la tarea como AUTH_REQUIRED con un indicador de finalidad.
     *
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    void requiresAuth(boolean isFinal);

    /**
     * Marca la tarea como AUTH_REQUIRED con un mensaje opcional e indicador de finalidad.
     *
     * @param message mensaje opcional a incluir
     * @param isFinal si este es un estado final (impide actualizaciones posteriores)
     */
    void requiresAuth(@Nullable Message message, boolean isFinal);

    /**
     * Crea un nuevo mensaje de agente con las partes y metadatos indicados.
     *
     * @param parts las partes a incluir en el mensaje
     * @param metadata metadatos opcionales a adjuntar al mensaje
     * @return un nuevo objeto Message listo para enviar
     */
    Message newAgentMessage(List<Part> parts, @Nullable Map<String, Object> metadata);

    /**
     * Envía un mensaje de texto simple al cliente.
     *
     * @param text el contenido de texto a enviar
     */
    void sendMessage(String text);

    /**
     * Envía un mensaje con partes personalizadas al cliente.
     *
     * @param parts las partes del mensaje a enviar
     */
    void sendMessage(List<Part> parts);

    /**
     * Envía un mensaje con partes y metadatos al cliente.
     *
     * @param parts las partes del mensaje a enviar
     * @param metadata metadatos opcionales a adjuntar al mensaje
     */
    void sendMessage(List<Part> parts, @Nullable Map<String, Object> metadata);

    /**
     * Envía un objeto Message existente directamente al cliente.
     *
     * @param message el mensaje a enviar al cliente
     * @since 1.0.0
     */
    void sendMessage(Message message);

    /**
     * Añade un objeto Task personalizado para enviarlo al cliente.
     *
     * @param task la tarea a añadir
     * @since 1.0.0
     */
    void addTask(Task task);

    /**
     * Crea un Task.Builder pre-poblado con los IDs de tarea y contexto correctos.
     *
     * @return un Task.Builder con id y contextId ya establecidos
     */
    Task.Builder taskBuilder();

    /**
     * Crea un Message.Builder pre-poblado con los valores por defecto del agente.
     *
     * @return un Message.Builder con los campos comunes del agente ya establecidos
     */
    Message.Builder messageBuilderXXX();

}