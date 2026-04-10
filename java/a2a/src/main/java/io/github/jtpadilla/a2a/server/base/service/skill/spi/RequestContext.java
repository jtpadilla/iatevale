package io.github.jtpadilla.a2a.server.base.service.skill.spi;

import com.google.lf.a2a.v1.SendMessageRequest;
import com.google.lf.a2a.v1.Task;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RequestContext {

    private final @Nullable SendMessageRequest params;
    private final String taskId;
    private final String contextId;
    private final @Nullable Task task;
    private final List<Task> relatedTasks;

    /**
     * Constructor con todos los campos ya validados e inicializados.
     *
     * @param request los parámetros de envío del mensaje (puede ser null en operaciones de cancelación)
     * @param taskId el identificador de la tarea (no puede ser null)
     * @param contextId el identificador del contexto (no puede ser null)
     * @param task el estado de la tarea existente (null para conversaciones nuevas)
     * @param relatedTasks otras tareas del mismo contexto (no puede ser null, puede estar vacío)
     */
    private RequestContext(
            @Nullable SendMessageRequest request,
            String taskId,
            String contextId,
            @Nullable Task task,
            List<Task> relatedTasks) {
        this.params = request;
        this.taskId = taskId;
        this.contextId = contextId;
        this.task = task;
        this.relatedTasks = relatedTasks;
    }

    /**
     * Devuelve el identificador de la tarea.
     * <p>
     * Es generado automáticamente (UUID) por el builder si el cliente no lo proporciona
     * en los parámetros del mensaje. Este valor nunca es null.
     * </p>
     *
     * @return el ID de la tarea (nunca null)
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Devuelve el identificador del contexto de conversación.
     * <p>
     * Los contextos de conversación agrupan tareas relacionadas (p.ej., múltiples tareas
     * en la misma sesión de usuario). Es generado automáticamente (UUID) por el builder si
     * el cliente no lo proporciona en los parámetros del mensaje. Este valor nunca es null.
     * </p>
     *
     * @return el ID de contexto (nunca null)
     */
    public String getContextId() {
        return contextId;
    }

    /**
     * Devuelve el estado de la tarea existente, si se trata de la continuación de una conversación.
     * <p>
     * Para conversaciones nuevas, es null. Para conversaciones en curso, contiene
     * el estado completo de la tarea incluyendo historial, artefactos y estado.
     * <p>
     * <b>Patrón habitual:</b>
     * <pre>{@code
     * if (context.getTask() == null) {
     *     // Conversación nueva - inicializar estado
     * } else {
     *     // Continuación - acceder a mensajes anteriores
     *     List<Message> history = context.getTask().history();
     * }
     * }</pre>
     *
     * @return la tarea existente, o null si es una conversación nueva
     */
    public @Nullable Task getTask() {
        return task;
    }

    /**
     * Devuelve otras tareas del mismo contexto de conversación.
     * <p>
     * Útil en conversaciones con múltiples tareas donde el agente necesita acceder
     * al estado de tareas relacionadas.
     * </p>
     *
     * @return lista no modificable de tareas relacionadas (vacía si no hay ninguna)
     */
    public List<Task> getRelatedTasks() {
        return Collections.unmodifiableList(relatedTasks);
    }

    /**
     * Asocia una tarea relacionada a este contexto.
     * <p>
     * Es usado principalmente por el framework para poblar las tareas relacionadas
     * tras la construcción. Las implementaciones de agente deben usar {@link #getRelatedTasks()}
     * para acceder a las tareas relacionadas.
     * </p>
     *
     * @param task la tarea a asociar
     */
    public void attachRelatedTask(Task task) {
        relatedTasks.add(task);
    }

}
