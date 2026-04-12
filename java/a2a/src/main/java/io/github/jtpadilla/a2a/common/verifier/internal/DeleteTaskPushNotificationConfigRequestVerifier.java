package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.DeleteTaskPushNotificationConfigRequest;

public class DeleteTaskPushNotificationConfigRequestVerifier {

    static public DeleteTaskPushNotificationConfigRequest verify(DeleteTaskPushNotificationConfigRequest msg) {
        return new DeleteTaskPushNotificationConfigRequestVerifier(msg).verify();
    }

    final private DeleteTaskPushNotificationConfigRequest msg;

    private DeleteTaskPushNotificationConfigRequestVerifier(DeleteTaskPushNotificationConfigRequest msg) {
        this.msg = msg;
    }

    private DeleteTaskPushNotificationConfigRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("DeleteTaskPushNotificationConfigRequest is null");
        }
        return msg;
    }

    private void verifyTaskId(DeleteTaskPushNotificationConfigRequest.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("DeleteTaskPushNotificationConfigRequest.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private void verifyId(DeleteTaskPushNotificationConfigRequest.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("DeleteTaskPushNotificationConfigRequest.id is required");
        }
        builder.setId(getThis().getId());
    }

    private DeleteTaskPushNotificationConfigRequest verify() {
        DeleteTaskPushNotificationConfigRequest.Builder builder = DeleteTaskPushNotificationConfigRequest.newBuilder();
        verifyTaskId(builder);
        verifyId(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        return builder.build();
    }

}
