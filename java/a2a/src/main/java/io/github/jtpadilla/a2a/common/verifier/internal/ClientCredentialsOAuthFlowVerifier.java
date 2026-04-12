package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.ClientCredentialsOAuthFlow;

public class ClientCredentialsOAuthFlowVerifier {

    static public ClientCredentialsOAuthFlow verify(ClientCredentialsOAuthFlow msg) {
        return new ClientCredentialsOAuthFlowVerifier(msg).verify();
    }

    final private ClientCredentialsOAuthFlow msg;

    private ClientCredentialsOAuthFlowVerifier(ClientCredentialsOAuthFlow msg) {
        this.msg = msg;
    }

    private ClientCredentialsOAuthFlow getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("ClientCredentialsOAuthFlow is null");
        }
        return msg;
    }

    private void verifyTokenUrl(ClientCredentialsOAuthFlow.Builder builder) {
        if (getThis().getTokenUrl().isBlank()) {
            throw new IllegalArgumentException("ClientCredentialsOAuthFlow.token_url is required");
        }
        builder.setTokenUrl(getThis().getTokenUrl());
    }

    private void verifyScopes(ClientCredentialsOAuthFlow.Builder builder) {
        if (getThis().getScopesMap().isEmpty()) {
            throw new IllegalArgumentException("ClientCredentialsOAuthFlow.scopes is required and must not be empty");
        }
        builder.putAllScopes(getThis().getScopesMap());
    }

    private ClientCredentialsOAuthFlow verify() {
        ClientCredentialsOAuthFlow.Builder builder = ClientCredentialsOAuthFlow.newBuilder();
        verifyTokenUrl(builder);
        verifyScopes(builder);
        if (!getThis().getRefreshUrl().isBlank()) {
            builder.setRefreshUrl(getThis().getRefreshUrl());
        }
        return builder.build();
    }

}
