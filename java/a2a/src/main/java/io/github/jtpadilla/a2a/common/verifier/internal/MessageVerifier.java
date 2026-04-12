package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.Message;
import com.google.lf.a2a.v1.Role;

public class MessageVerifier {

    static public Message verify(Message msg) {
        return new MessageVerifier(msg).verify();
    }

    final private Message msg;

    private MessageVerifier(Message msg) {
        this.msg = msg;
    }

    private Message getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("Message is null");
        }
        return msg;
    }

    private void verifyMessageId(Message.Builder builder) {
        if (getThis().getMessageId().isBlank()) {
            throw new IllegalArgumentException("Message.message_id is required");
        }
        builder.setMessageId(getThis().getMessageId());
    }

    private void verifyRole(Message.Builder builder) {
        if (getThis().getRole() == Role.ROLE_UNSPECIFIED) {
            throw new IllegalArgumentException("Message.role is required");
        }
        builder.setRole(getThis().getRole());
    }

    private void verifyParts(Message.Builder builder) {
        if (getThis().getPartsList().isEmpty()) {
            throw new IllegalArgumentException("Message.parts is required and must not be empty");
        }
        builder.addAllParts(getThis().getPartsList());
    }

    private Message verify() {
        Message.Builder builder = Message.newBuilder();
        verifyMessageId(builder);
        verifyRole(builder);
        verifyParts(builder);
        if (!getThis().getContextId().isBlank()) {
            builder.setContextId(getThis().getContextId());
        }
        if (!getThis().getTaskId().isBlank()) {
            builder.setTaskId(getThis().getTaskId());
        }
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        builder.addAllExtensions(getThis().getExtensionsList());
        builder.addAllReferenceTaskIds(getThis().getReferenceTaskIdsList());
        return builder.build();
    }

}
