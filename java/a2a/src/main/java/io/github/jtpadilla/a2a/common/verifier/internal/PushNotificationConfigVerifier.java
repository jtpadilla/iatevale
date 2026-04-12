package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.PushNotificationConfig;

public class PushNotificationConfigVerifier {

    static public PushNotificationConfig verify(PushNotificationConfig msg) {
        return new PushNotificationConfigVerifier(msg).verify();
    }

    final private PushNotificationConfig msg;

    private PushNotificationConfigVerifier(PushNotificationConfig msg) {
        this.msg = msg;
    }

    private PushNotificationConfig getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("PushNotificationConfig is null");
        }
        return msg;
    }

    private void verifyUrl(PushNotificationConfig.Builder builder) {
        if (getThis().getUrl().isBlank()) {
            throw new IllegalArgumentException("PushNotificationConfig.url is required");
        }
        builder.setUrl(getThis().getUrl());
    }

    private PushNotificationConfig verify() {
        PushNotificationConfig.Builder builder = PushNotificationConfig.newBuilder();
        verifyUrl(builder);
        if (!getThis().getId().isBlank()) {
            builder.setId(getThis().getId());
        }
        if (!getThis().getToken().isBlank()) {
            builder.setToken(getThis().getToken());
        }
        if (getThis().hasAuthentication()) {
            builder.setAuthentication(getThis().getAuthentication());
        }
        return builder.build();
    }

}
