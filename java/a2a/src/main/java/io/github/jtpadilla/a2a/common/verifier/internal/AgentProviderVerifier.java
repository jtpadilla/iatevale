package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AgentProvider;

public class AgentProviderVerifier {

    static public AgentProvider verify(AgentProvider msg) {
        return new AgentProviderVerifier(msg).verify();
    }

    final private AgentProvider msg;

    private AgentProviderVerifier(AgentProvider msg) {
        this.msg = msg;
    }

    private AgentProvider getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AgentProvider is null");
        }
        return msg;
    }

    private void verifyUrl(AgentProvider.Builder builder) {
        if (getThis().getUrl().isBlank()) {
            throw new IllegalArgumentException("AgentProvider.url is required");
        }
        builder.setUrl(getThis().getUrl());
    }

    private void verifyOrganization(AgentProvider.Builder builder) {
        if (getThis().getOrganization().isBlank()) {
            throw new IllegalArgumentException("AgentProvider.organization is required");
        }
        builder.setOrganization(getThis().getOrganization());
    }

    private AgentProvider verify() {
        AgentProvider.Builder builder = AgentProvider.newBuilder();
        verifyUrl(builder);
        verifyOrganization(builder);
        return builder.build();
    }

}
