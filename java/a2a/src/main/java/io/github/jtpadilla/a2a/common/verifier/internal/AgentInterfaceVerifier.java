package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AgentInterface;

public class AgentInterfaceVerifier {

    static public AgentInterface verify(AgentInterface msg) {
        return new AgentInterfaceVerifier(msg).verify();
    }

    final private AgentInterface msg;

    private AgentInterfaceVerifier(AgentInterface msg) {
        this.msg = msg;
    }

    private AgentInterface getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AgentInterface is null");
        }
        return msg;
    }

    private void verifyUrl(AgentInterface.Builder builder) {
        if (getThis().getUrl().isBlank()) {
            throw new IllegalArgumentException("AgentInterface.url is required");
        }
        builder.setUrl(getThis().getUrl());
    }

    private void verifyProtocolBinding(AgentInterface.Builder builder) {
        if (getThis().getProtocolBinding().isBlank()) {
            throw new IllegalArgumentException("AgentInterface.protocol_binding is required");
        }
        builder.setProtocolBinding(getThis().getProtocolBinding());
    }

    private void verifyProtocolVersion(AgentInterface.Builder builder) {
        if (getThis().getProtocolVersion().isBlank()) {
            throw new IllegalArgumentException("AgentInterface.protocol_version is required");
        }
        builder.setProtocolVersion(getThis().getProtocolVersion());
    }

    private AgentInterface verify() {
        AgentInterface.Builder builder = AgentInterface.newBuilder();
        verifyUrl(builder);
        verifyProtocolBinding(builder);
        verifyProtocolVersion(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        return builder.build();
    }

}
