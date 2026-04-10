package io.github.jtpadilla.a2a.server.base.service.skill.spi;

import com.google.lf.a2a.v1.*;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
public interface AgentEmitter {

    /**
     * Actualiza el estado de la tarea al estado indicado con un mensaje opcional.
     *
     * @param taskState el nuevo estado de la tarea
     * @param message mensaje opcional a incluir con la actualización de estado
     */
    void updateStatus(TaskState taskState, @Nullable Message message);

    /**
     * Devuelve el ID de contexto de este emitter.
     *
     * @return el ID de contexto, o null si no está disponible
     */
    @Nullable String getContextId();

    /**
     * Devuelve el ID de tarea de este emitter.
     *
     * @return el ID de tarea, o null si no hay tarea asociada
     */
    @Nullable String getTaskId();

    /**
     * Añade un artefacto con las partes indicadas a la tarea.
     *
     * @param parts las partes a incluir en el artefacto
     */
    void addArtifact(List<Part<?>> parts);

    /**
     * Añade un artefacto con las partes, ID de artefacto, nombre y metadatos indicados.
     *
     * @param parts las partes a incluir en el artefacto
     * @param artifactId ID de artefacto opcional (se genera si es null)
     * @param name nombre opcional del artefacto
     * @param metadata mapa de metadatos opcional
     */
    void addArtifact(List<Part> parts, @Nullable String artifactId, @Nullable String name, @Nullable Map<String, Object> metadata);

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
    void addArtifact(List<Part> parts, @Nullable String artifactId, @Nullable String name, @Nullable Map<String, Object> metadata,
                     @Nullable Boolean append, @Nullable Boolean lastChunk);

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
    Message newAgentMessage(List<Part<?>> parts, @Nullable Map<String, Object> metadata);

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
    void sendMessage(List<Part<?>> parts);

    /**
     * Envía un mensaje con partes y metadatos al cliente.
     *
     * @param parts las partes del mensaje a enviar
     * @param metadata metadatos opcionales a adjuntar al mensaje
     */
    void sendMessage(List<Part<?>> parts, @Nullable Map<String, Object> metadata);

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
     * Emite un objeto Event personalizado al cliente.
     *
     * @param event el evento a emitir
     * @since 1.0.0
     */
    void emitEvent(Event event);

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
    Message.Builder messageBuilder();

}