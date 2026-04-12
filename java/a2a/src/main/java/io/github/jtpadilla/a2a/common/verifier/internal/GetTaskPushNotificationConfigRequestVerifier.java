package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.GetTaskPushNotificationConfigRequest;

public class GetTaskPushNotificationConfigRequestVerifier {

    static public GetTaskPushNotificationConfigRequest verify(GetTaskPushNotificationConfigRequest msg) {
        return new GetTaskPushNotificationConfigRequestVerifier(msg).verify();
    }

    final private GetTaskPushNotificationConfigRequest msg;

    private GetTaskPushNotificationConfigRequestVerifier(GetTaskPushNotificationConfigRequest msg) {
        this.msg = msg;
    }

    private GetTaskPushNotificationConfigRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("GetTaskPushNotificationConfigRequest is null");
        }
        return msg;
    }

    private void verifyTaskId(GetTaskPushNotificationConfigRequest.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("GetTaskPushNotificationConfigRequest.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private void verifyId(GetTaskPushNotificationConfigRequest.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("GetTaskPushNotificationConfigRequest.id is required");
        }
        builder.setId(getThis().getId());
    }

    private GetTaskPushNotificationConfigRequest verify() {
        GetTaskPushNotificationConfigRequest.Builder builder = GetTaskPushNotificationConfigRequest.newBuilder();
        verifyTaskId(builder);
        verifyId(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        return builder.build();
    }

}
