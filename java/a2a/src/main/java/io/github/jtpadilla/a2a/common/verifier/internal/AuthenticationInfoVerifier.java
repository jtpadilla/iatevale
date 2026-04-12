package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AuthenticationInfo;

public class AuthenticationInfoVerifier {

    static public AuthenticationInfo verify(AuthenticationInfo msg) {
        return new AuthenticationInfoVerifier(msg).verify();
    }

    final private AuthenticationInfo msg;

    private AuthenticationInfoVerifier(AuthenticationInfo msg) {
        this.msg = msg;
    }

    private AuthenticationInfo getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AuthenticationInfo is null");
        }
        return msg;
    }

    private void verifyScheme(AuthenticationInfo.Builder builder) {
        if (getThis().getScheme().isBlank()) {
            throw new IllegalArgumentException("AuthenticationInfo.scheme is required");
        }
        builder.setScheme(getThis().getScheme());
    }

    private AuthenticationInfo verify() {
        AuthenticationInfo.Builder builder = AuthenticationInfo.newBuilder();
        verifyScheme(builder);
        if (!getThis().getCredentials().isBlank()) {
            builder.setCredentials(getThis().getCredentials());
        }
        return builder.build();
    }

}
