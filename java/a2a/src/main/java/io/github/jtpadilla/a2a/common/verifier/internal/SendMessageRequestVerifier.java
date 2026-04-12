package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.SendMessageRequest;

public class SendMessageRequestVerifier {

    static public SendMessageRequest verify(SendMessageRequest msg) {
        return new SendMessageRequestVerifier(msg).verify();
    }

    final private SendMessageRequest msg;

    private SendMessageRequestVerifier(SendMessageRequest msg) {
        this.msg = msg;
    }

    private SendMessageRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("SendMessageRequest is null");
        }
        return msg;
    }

    private void verifyMessage(SendMessageRequest.Builder builder) {
        if (!getThis().hasMessage()) {
            throw new IllegalArgumentException("SendMessageRequest.message is required");
        }
        builder.setMessage(getThis().getMessage());
    }

    private SendMessageRequest verify() {
        SendMessageRequest.Builder builder = SendMessageRequest.newBuilder();
        verifyMessage(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        if (getThis().hasConfiguration()) {
            builder.setConfiguration(getThis().getConfiguration());
        }
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        return builder.build();
    }

}
