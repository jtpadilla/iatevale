package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.TaskStatusUpdateEvent;

public class TaskStatusUpdateEventVerifier {

    static public TaskStatusUpdateEvent verify(TaskStatusUpdateEvent msg) {
        return new TaskStatusUpdateEventVerifier(msg).verify();
    }

    final private TaskStatusUpdateEvent msg;

    private TaskStatusUpdateEventVerifier(TaskStatusUpdateEvent msg) {
        this.msg = msg;
    }

    private TaskStatusUpdateEvent getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("TaskStatusUpdateEvent is null");
        }
        return msg;
    }

    private void verifyTaskId(TaskStatusUpdateEvent.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("TaskStatusUpdateEvent.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private void verifyContextId(TaskStatusUpdateEvent.Builder builder) {
        if (getThis().getContextId().isBlank()) {
            throw new IllegalArgumentException("TaskStatusUpdateEvent.context_id is required");
        }
        builder.setContextId(getThis().getContextId());
    }

    private void verifyStatus(TaskStatusUpdateEvent.Builder builder) {
        if (!getThis().hasStatus()) {
            throw new IllegalArgumentException("TaskStatusUpdateEvent.status is required");
        }
        builder.setStatus(getThis().getStatus());
    }

    private TaskStatusUpdateEvent verify() {
        TaskStatusUpdateEvent.Builder builder = TaskStatusUpdateEvent.newBuilder();
        verifyTaskId(builder);
        verifyContextId(builder);
        verifyStatus(builder);
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        return builder.build();
    }

}
