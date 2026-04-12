package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.HTTPAuthSecurityScheme;

public class HTTPAuthSecuritySchemeVerifier {

    static public HTTPAuthSecurityScheme verify(HTTPAuthSecurityScheme msg) {
        return new HTTPAuthSecuritySchemeVerifier(msg).verify();
    }

    final private HTTPAuthSecurityScheme msg;

    private HTTPAuthSecuritySchemeVerifier(HTTPAuthSecurityScheme msg) {
        this.msg = msg;
    }

    private HTTPAuthSecurityScheme getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("HTTPAuthSecurityScheme is null");
        }
        return msg;
    }

    private void verifyScheme(HTTPAuthSecurityScheme.Builder builder) {
        if (getThis().getScheme().isBlank()) {
            throw new IllegalArgumentException("HTTPAuthSecurityScheme.scheme is required");
        }
        builder.setScheme(getThis().getScheme());
    }

    private HTTPAuthSecurityScheme verify() {
        HTTPAuthSecurityScheme.Builder builder = HTTPAuthSecurityScheme.newBuilder();
        verifyScheme(builder);
        if (!getThis().getDescription().isBlank()) {
            builder.setDescription(getThis().getDescription());
        }
        if (!getThis().getBearerFormat().isBlank()) {
            builder.setBearerFormat(getThis().getBearerFormat());
        }
        return builder.build();
    }

}
