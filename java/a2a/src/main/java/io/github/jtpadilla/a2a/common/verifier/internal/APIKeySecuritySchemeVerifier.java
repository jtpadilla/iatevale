package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.APIKeySecurityScheme;

public class APIKeySecuritySchemeVerifier {

    static public APIKeySecurityScheme verify(APIKeySecurityScheme msg) {
        return new APIKeySecuritySchemeVerifier(msg).verify();
    }

    final private APIKeySecurityScheme msg;

    private APIKeySecuritySchemeVerifier(APIKeySecurityScheme msg) {
        this.msg = msg;
    }

    private APIKeySecurityScheme getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("APIKeySecurityScheme is null");
        }
        return msg;
    }

    private void verifyLocation(APIKeySecurityScheme.Builder builder) {
        if (getThis().getLocation().isBlank()) {
            throw new IllegalArgumentException("APIKeySecurityScheme.location is required");
        }
        builder.setLocation(getThis().getLocation());
    }

    private void verifyName(APIKeySecurityScheme.Builder builder) {
        if (getThis().getName().isBlank()) {
            throw new IllegalArgumentException("APIKeySecurityScheme.name is required");
        }
        builder.setName(getThis().getName());
    }

    private APIKeySecurityScheme verify() {
        APIKeySecurityScheme.Builder builder = APIKeySecurityScheme.newBuilder();
        verifyLocation(builder);
        verifyName(builder);
        if (!getThis().getDescription().isBlank()) {
            builder.setDescription(getThis().getDescription());
        }
        return builder.build();
    }

}
