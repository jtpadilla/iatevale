package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.SubscribeToTaskRequest;

public class SubscribeToTaskRequestVerifier {

    static public SubscribeToTaskRequest verify(SubscribeToTaskRequest msg) {
        return new SubscribeToTaskRequestVerifier(msg).verify();
    }

    final private SubscribeToTaskRequest msg;

    private SubscribeToTaskRequestVerifier(SubscribeToTaskRequest msg) {
        this.msg = msg;
    }

    private SubscribeToTaskRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("SubscribeToTaskRequest is null");
        }
        return msg;
    }

    private void verifyId(SubscribeToTaskRequest.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("SubscribeToTaskRequest.id is required");
        }
        builder.setId(getThis().getId());
    }

    private SubscribeToTaskRequest verify() {
        SubscribeToTaskRequest.Builder builder = SubscribeToTaskRequest.newBuilder();
        verifyId(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        return builder.build();
    }

}
