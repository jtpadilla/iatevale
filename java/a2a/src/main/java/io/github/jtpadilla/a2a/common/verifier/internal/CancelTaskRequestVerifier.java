package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.CancelTaskRequest;

public class CancelTaskRequestVerifier {

    static public CancelTaskRequest verify(CancelTaskRequest msg) {
        return new CancelTaskRequestVerifier(msg).verify();
    }

    final private CancelTaskRequest msg;

    private CancelTaskRequestVerifier(CancelTaskRequest msg) {
        this.msg = msg;
    }

    private CancelTaskRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("CancelTaskRequest is null");
        }
        return msg;
    }

    private void verifyId(CancelTaskRequest.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("CancelTaskRequest.id is required");
        }
        builder.setId(getThis().getId());
    }

    private CancelTaskRequest verify() {
        CancelTaskRequest.Builder builder = CancelTaskRequest.newBuilder();
        verifyId(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        return builder.build();
    }

}
