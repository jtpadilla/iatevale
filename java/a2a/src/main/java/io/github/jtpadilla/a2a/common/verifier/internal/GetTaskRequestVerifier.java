package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.GetTaskRequest;

public class GetTaskRequestVerifier {

    static public GetTaskRequest verify(GetTaskRequest msg) {
        return new GetTaskRequestVerifier(msg).verify();
    }

    final private GetTaskRequest msg;

    private GetTaskRequestVerifier(GetTaskRequest msg) {
        this.msg = msg;
    }

    private GetTaskRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("GetTaskRequest is null");
        }
        return msg;
    }

    private void verifyId(GetTaskRequest.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("GetTaskRequest.id is required");
        }
        builder.setId(getThis().getId());
    }

    private GetTaskRequest verify() {
        GetTaskRequest.Builder builder = GetTaskRequest.newBuilder();
        verifyId(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        if (getThis().hasHistoryLength()) {
            builder.setHistoryLength(getThis().getHistoryLength());
        }
        return builder.build();
    }

}
