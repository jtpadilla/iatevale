package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AgentCardSignature;

public class AgentCardSignatureVerifier {

    static public AgentCardSignature verify(AgentCardSignature msg) {
        return new AgentCardSignatureVerifier(msg).verify();
    }

    final private AgentCardSignature msg;

    private AgentCardSignatureVerifier(AgentCardSignature msg) {
        this.msg = msg;
    }

    private AgentCardSignature getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AgentCardSignature is null");
        }
        return msg;
    }

    private void verifyProtected(AgentCardSignature.Builder builder) {
        if (getThis().getProtected().isBlank()) {
            throw new IllegalArgumentException("AgentCardSignature.protected is required");
        }
        builder.setProtected(getThis().getProtected());
    }

    private void verifySignature(AgentCardSignature.Builder builder) {
        if (getThis().getSignature().isBlank()) {
            throw new IllegalArgumentException("AgentCardSignature.signature is required");
        }
        builder.setSignature(getThis().getSignature());
    }

    private AgentCardSignature verify() {
        AgentCardSignature.Builder builder = AgentCardSignature.newBuilder();
        verifyProtected(builder);
        verifySignature(builder);
        if (getThis().hasHeader()) {
            builder.setHeader(getThis().getHeader());
        }
        return builder.build();
    }

}
