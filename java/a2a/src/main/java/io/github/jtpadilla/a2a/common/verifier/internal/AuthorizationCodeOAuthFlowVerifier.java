package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AuthorizationCodeOAuthFlow;

public class AuthorizationCodeOAuthFlowVerifier {

    static public AuthorizationCodeOAuthFlow verify(AuthorizationCodeOAuthFlow msg) {
        return new AuthorizationCodeOAuthFlowVerifier(msg).verify();
    }

    final private AuthorizationCodeOAuthFlow msg;

    private AuthorizationCodeOAuthFlowVerifier(AuthorizationCodeOAuthFlow msg) {
        this.msg = msg;
    }

    private AuthorizationCodeOAuthFlow getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AuthorizationCodeOAuthFlow is null");
        }
        return msg;
    }

    private void verifyAuthorizationUrl(AuthorizationCodeOAuthFlow.Builder builder) {
        if (getThis().getAuthorizationUrl().isBlank()) {
            throw new IllegalArgumentException("AuthorizationCodeOAuthFlow.authorization_url is required");
        }
        builder.setAuthorizationUrl(getThis().getAuthorizationUrl());
    }

    private void verifyTokenUrl(AuthorizationCodeOAuthFlow.Builder builder) {
        if (getThis().getTokenUrl().isBlank()) {
            throw new IllegalArgumentException("AuthorizationCodeOAuthFlow.token_url is required");
        }
        builder.setTokenUrl(getThis().getTokenUrl());
    }

    private void verifyScopes(AuthorizationCodeOAuthFlow.Builder builder) {
        if (getThis().getScopesMap().isEmpty()) {
            throw new IllegalArgumentException("AuthorizationCodeOAuthFlow.scopes is required and must not be empty");
        }
        builder.putAllScopes(getThis().getScopesMap());
    }

    private AuthorizationCodeOAuthFlow verify() {
        AuthorizationCodeOAuthFlow.Builder builder = AuthorizationCodeOAuthFlow.newBuilder();
        verifyAuthorizationUrl(builder);
        verifyTokenUrl(builder);
        verifyScopes(builder);
        if (!getThis().getRefreshUrl().isBlank()) {
            builder.setRefreshUrl(getThis().getRefreshUrl());
        }
        builder.setPkceRequired(getThis().getPkceRequired());
        return builder.build();
    }

}
