package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.ListTaskPushNotificationConfigsRequest;

public class ListTaskPushNotificationConfigsRequestVerifier {

    static public ListTaskPushNotificationConfigsRequest verify(ListTaskPushNotificationConfigsRequest msg) {
        return new ListTaskPushNotificationConfigsRequestVerifier(msg).verify();
    }

    final private ListTaskPushNotificationConfigsRequest msg;

    private ListTaskPushNotificationConfigsRequestVerifier(ListTaskPushNotificationConfigsRequest msg) {
        this.msg = msg;
    }

    private ListTaskPushNotificationConfigsRequest getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("ListTaskPushNotificationConfigsRequest is null");
        }
        return msg;
    }

    private void verifyTaskId(ListTaskPushNotificationConfigsRequest.Builder builder) {
        if (getThis().getTaskId().isBlank()) {
            throw new IllegalArgumentException("ListTaskPushNotificationConfigsRequest.task_id is required");
        }
        builder.setTaskId(getThis().getTaskId());
    }

    private ListTaskPushNotificationConfigsRequest verify() {
        ListTaskPushNotificationConfigsRequest.Builder builder = ListTaskPushNotificationConfigsRequest.newBuilder();
        verifyTaskId(builder);
        if (!getThis().getTenant().isBlank()) {
            builder.setTenant(getThis().getTenant());
        }
        builder.setPageSize(getThis().getPageSize());
        if (!getThis().getPageToken().isBlank()) {
            builder.setPageToken(getThis().getPageToken());
        }
        return builder.build();
    }

}
