package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.DeviceCodeOAuthFlow;

public class DeviceCodeOAuthFlowVerifier {

    static public DeviceCodeOAuthFlow verify(DeviceCodeOAuthFlow msg) {
        return new DeviceCodeOAuthFlowVerifier(msg).verify();
    }

    final private DeviceCodeOAuthFlow msg;

    private DeviceCodeOAuthFlowVerifier(DeviceCodeOAuthFlow msg) {
        this.msg = msg;
    }

    private DeviceCodeOAuthFlow getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("DeviceCodeOAuthFlow is null");
        }
        return msg;
    }

    private void verifyDeviceAuthorizationUrl(DeviceCodeOAuthFlow.Builder builder) {
        if (getThis().getDeviceAuthorizationUrl().isBlank()) {
            throw new IllegalArgumentException("DeviceCodeOAuthFlow.device_authorization_url is required");
        }
        builder.setDeviceAuthorizationUrl(getThis().getDeviceAuthorizationUrl());
    }

    private void verifyTokenUrl(DeviceCodeOAuthFlow.Builder builder) {
        if (getThis().getTokenUrl().isBlank()) {
            throw new IllegalArgumentException("DeviceCodeOAuthFlow.token_url is required");
        }
        builder.setTokenUrl(getThis().getTokenUrl());
    }

    private void verifyScopes(DeviceCodeOAuthFlow.Builder builder) {
        if (getThis().getScopesMap().isEmpty()) {
            throw new IllegalArgumentException("DeviceCodeOAuthFlow.scopes is required and must not be empty");
        }
        builder.putAllScopes(getThis().getScopesMap());
    }

    private DeviceCodeOAuthFlow verify() {
        DeviceCodeOAuthFlow.Builder builder = DeviceCodeOAuthFlow.newBuilder();
        verifyDeviceAuthorizationUrl(builder);
        verifyTokenUrl(builder);
        verifyScopes(builder);
        if (!getThis().getRefreshUrl().isBlank()) {
            builder.setRefreshUrl(getThis().getRefreshUrl());
        }
        return builder.build();
    }

}
