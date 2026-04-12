package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.OAuth2SecurityScheme;

public class OAuth2SecuritySchemeVerifier {

    static public OAuth2SecurityScheme verify(OAuth2SecurityScheme msg) {
        return new OAuth2SecuritySchemeVerifier(msg).verify();
    }

    final private OAuth2SecurityScheme msg;

    private OAuth2SecuritySchemeVerifier(OAuth2SecurityScheme msg) {
        this.msg = msg;
    }

    private OAuth2SecurityScheme getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("OAuth2SecurityScheme is null");
        }
        return msg;
    }

    private void verifyFlows(OAuth2SecurityScheme.Builder builder) {
        if (!getThis().hasFlows()) {
            throw new IllegalArgumentException("OAuth2SecurityScheme.flows is required");
        }
        builder.setFlows(getThis().getFlows());
    }

    private OAuth2SecurityScheme verify() {
        OAuth2SecurityScheme.Builder builder = OAuth2SecurityScheme.newBuilder();
        verifyFlows(builder);
        if (!getThis().getDescription().isBlank()) {
            builder.setDescription(getThis().getDescription());
        }
        if (!getThis().getOauth2MetadataUrl().isBlank()) {
            builder.setOauth2MetadataUrl(getThis().getOauth2MetadataUrl());
        }
        return builder.build();
    }

}
