package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.OpenIdConnectSecurityScheme;

public class OpenIdConnectSecuritySchemeVerifier {

    static public OpenIdConnectSecurityScheme verify(OpenIdConnectSecurityScheme msg) {
        return new OpenIdConnectSecuritySchemeVerifier(msg).verify();
    }

    final private OpenIdConnectSecurityScheme msg;

    private OpenIdConnectSecuritySchemeVerifier(OpenIdConnectSecurityScheme msg) {
        this.msg = msg;
    }

    private OpenIdConnectSecurityScheme getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("OpenIdConnectSecurityScheme is null");
        }
        return msg;
    }

    private void verifyOpenIdConnectUrl(OpenIdConnectSecurityScheme.Builder builder) {
        if (getThis().getOpenIdConnectUrl().isBlank()) {
            throw new IllegalArgumentException("OpenIdConnectSecurityScheme.open_id_connect_url is required");
        }
        builder.setOpenIdConnectUrl(getThis().getOpenIdConnectUrl());
    }

    private OpenIdConnectSecurityScheme verify() {
        OpenIdConnectSecurityScheme.Builder builder = OpenIdConnectSecurityScheme.newBuilder();
        verifyOpenIdConnectUrl(builder);
        if (!getThis().getDescription().isBlank()) {
            builder.setDescription(getThis().getDescription());
        }
        return builder.build();
    }

}
