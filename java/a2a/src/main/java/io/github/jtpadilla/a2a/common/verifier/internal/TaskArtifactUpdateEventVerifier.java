package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.TaskArtifactUpdateEvent;

public class TaskArtifactUpdateEventVerifier {

    static public TaskArtifactUpdateEvent verify(TaskArtifactUpdateEvent msg) {
        return new TaskArtifactUpdateEventVerifier(msg).verify();
    }

    final private TaskArtifactUpdateEvent msg;

    private TaskArtifactUpdateEventVerifier(TaskArtifactUpdateEvent msg) {
        this.msg = msg;
    }

    private TaskArtifactUpdateEvent getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("TaskArtifactUpdateEvent is null");
        }
        return msg;
    }

    private void verifyTaskId(TaskArtifactUpdateEvent.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("TaskArtifactUpdateEvent.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private void verifyContextId(TaskArtifactUpdateEvent.Builder builder) {
        if (getThis().getContextId().isBlank()) {
            throw new IllegalArgumentException("TaskArtifactUpdateEvent.context_id is required");
        }
        builder.setContextId(getThis().getContextId());
    }

    private void verifyArtifact(TaskArtifactUpdateEvent.Builder builder) {
        if (!getThis().hasArtifact()) {
            throw new IllegalArgumentException("TaskArtifactUpdateEvent.artifact is required");
        }
        builder.setArtifact(getThis().getArtifact());
    }

    private TaskArtifactUpdateEvent verify() {
        TaskArtifactUpdateEvent.Builder builder = TaskArtifactUpdateEvent.newBuilder();
        verifyTaskId(builder);
        verifyContextId(builder);
        verifyArtifact(builder);
        builder.setAppend(getThis().getAppend());
        builder.setLastChunk(getThis().getLastChunk());
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        return builder.build();
    }

}
