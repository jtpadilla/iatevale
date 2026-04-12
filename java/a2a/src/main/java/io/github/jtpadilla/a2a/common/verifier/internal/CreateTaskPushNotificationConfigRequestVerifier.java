package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.CreateTaskPushNotificationConfigRequest;

public class CreateTaskPushNotificationConfigRequestVerifier {

    static public CreateTaskPushNotificationConfigRequest verify(CreateTaskPushNotificationConfigRequest msg) {
        return new CreateTaskPushNotificationConfigRequestVerifier(msg).verify();
    }

    final private CreateTaskPushNotificationConfigRequest msg;

    private CreateTaskPushNotificationConfigRequestVerifier(CreateTaskPushNotificationConfigRequest msg) {
        this.msg = msg;
    }

    private CreateTaskPushNotificationConfigRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("CreateTaskPushNotificationConfigRequest is null");
        }
        return msg;
    }

    private void verifyTaskId(CreateTaskPushNotificationConfigRequest.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("CreateTaskPushNotificationConfigRequest.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private void verifyConfig(CreateTaskPushNotificationConfigRequest.Builder builder) {
        if (!getThis().hasConfig()) {
            throw new IllegalArgumentException("CreateTaskPushNotificationConfigRequest.config is required");
        }
        builder.setConfig(getThis().getConfig());
    }

    private CreateTaskPushNotificationConfigRequest verify() {
        CreateTaskPushNotificationConfigRequest.Builder builder = CreateTaskPushNotificationConfigRequest.newBuilder();
        verifyTaskId(builder);
        verifyConfig(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        return builder.build();
    }

}
