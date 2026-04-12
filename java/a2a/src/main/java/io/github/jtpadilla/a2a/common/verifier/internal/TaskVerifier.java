package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.Task;

public class TaskVerifier {

    static public Task verify(Task msg) {
        return new TaskVerifier(msg).verify();
    }

    final private Task msg;

    private TaskVerifier(Task msg) {
        this.msg = msg;
    }

    private Task getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("Task is null");
        }
        return msg;
    }

    private void verifyId(Task.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("Task.id is required");
        }
        builder.setId(getThis().getId());
    }

    private void verifyContextId(Task.Builder builder) {
        if (getThis().getContextId().isBlank()) {
            throw new IllegalArgumentException("Task.context_id is required");
        }
        builder.setContextId(getThis().getContextId());
    }

    private void verifyStatus(Task.Builder builder) {
        if (!getThis().hasStatus()) {
            throw new IllegalArgumentException("Task.status is required");
        }
        builder.setStatus(getThis().getStatus());
    }

    private Task verify() {
        Task.Builder builder = Task.newBuilder();
        verifyId(builder);
        verifyContextId(builder);
        verifyStatus(builder);
        builder.addAllArtifacts(getThis().getArtifactsList());
        builder.addAllHistory(getThis().getHistoryList());
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        return builder.build();
    }

}
