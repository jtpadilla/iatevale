package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.TaskPushNotificationConfig;

public class TaskPushNotificationConfigVerifier {

    static public TaskPushNotificationConfig verify(TaskPushNotificationConfig msg) {
        return new TaskPushNotificationConfigVerifier(msg).verify();
    }

    final private TaskPushNotificationConfig msg;

    private TaskPushNotificationConfigVerifier(TaskPushNotificationConfig msg) {
        this.msg = msg;
    }

    private TaskPushNotificationConfig getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("TaskPushNotificationConfig is null");
        }
        return msg;
    }

    private void verifyTaskId(TaskPushNotificationConfig.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("TaskPushNotificationConfig.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private void verifyPushNotificationConfig(TaskPushNotificationConfig.Builder builder) {
        if (!getThis().hasPushNotificationConfig()) {
            throw new IllegalArgumentException("TaskPushNotificationConfig.push_notification_config is required");
        }
        builder.setPushNotificationConfig(getThis().getPushNotificationConfig());
    }

    private TaskPushNotificationConfig verify() {
        TaskPushNotificationConfig.Builder builder = TaskPushNotificationConfig.newBuilder();
        verifyTaskId(builder);
        verifyPushNotificationConfig(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        return builder.build();
    }

}
