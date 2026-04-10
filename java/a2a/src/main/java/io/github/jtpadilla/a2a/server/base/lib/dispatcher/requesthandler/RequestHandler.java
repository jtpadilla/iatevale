package io.github.jtpadilla.a2a.server.base.lib.dispatcher.requesthandler;

import io.a2a.jsonrpc.common.wrappers.ListTasksResult;
import io.a2a.server.ServerCallContext;
import io.a2a.spec.*;

import java.util.concurrent.Flow;

public interface RequestHandler {
    Task onGetTask(
            TaskQueryParams params,
            ServerCallContext context) throws A2AError;

    ListTasksResult onListTasks(
            ListTasksParams params,
            ServerCallContext context) throws A2AError;

    Task onCancelTask(
            CancelTaskParams params,
            ServerCallContext context) throws A2AError;

    EventKind onMessageSend(
            MessageSendParams params,
            ServerCallContext context) throws A2AError;

    Flow.Publisher<StreamingEventKind> onMessageSendStream(
            MessageSendParams params,
            ServerCallContext context) throws A2AError;

    TaskPushNotificationConfig onCreateTaskPushNotificationConfig(
            TaskPushNotificationConfig params,
            ServerCallContext context) throws A2AError;

    TaskPushNotificationConfig onGetTaskPushNotificationConfig(
            GetTaskPushNotificationConfigParams params,
            ServerCallContext context) throws A2AError;

    Flow.Publisher<StreamingEventKind> onSubscribeToTask(
            TaskIdParams params,
            ServerCallContext context) throws A2AError;

    ListTaskPushNotificationConfigsResult onListTaskPushNotificationConfigs(
            ListTaskPushNotificationConfigsParams params,
            ServerCallContext context) throws A2AError;

    void onDeleteTaskPushNotificationConfig(
            DeleteTaskPushNotificationConfigParams params,
            ServerCallContext context) throws A2AError;
}
