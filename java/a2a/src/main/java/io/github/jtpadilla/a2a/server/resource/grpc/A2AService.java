package io.github.jtpadilla.a2a.server.resource.grpc;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.lib.operations.Operations;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.logging.Logger;

@Service.Singleton
public class A2AService extends A2AServiceGrpc.A2AServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(A2AService.class.getName());

    final private Operations operations;

    @Service.Inject
    public A2AService(Operations operations) {
        this.operations = operations;
    }

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        operations.sendMessage(request, responseObserver);
    }

    @Override
    public void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        operations.sendStreamingMessage(request, responseObserver);
    }

    @Override
    public void getExtendedAgentCard(GetExtendedAgentCardRequest request, StreamObserver<AgentCard> responseObserver) {
        operations.getExtendedAgentCard(request, responseObserver);
    }

    /*

    @Override
    public void getTask(GetTaskRequest request, StreamObserver<Task> responseObserver) {
    }

    @Override
    public void listTasks(ListTasksRequest request, StreamObserver<ListTasksResponse> responseObserver) {
    }

    @Override
    public void cancelTask(CancelTaskRequest request, StreamObserver<Task> responseObserver) {
    }

    @Override
    public void subscribeToTask(SubscribeToTaskRequest request, StreamObserver<StreamResponse> responseObserver) {
    }

    @Override
    public void createTaskPushNotificationConfig(CreateTaskPushNotificationConfigRequest request, StreamObserver<TaskPushNotificationConfig> responseObserver) {
    }

    @Override
    public void getTaskPushNotificationConfig(GetTaskPushNotificationConfigRequest request, StreamObserver<TaskPushNotificationConfig> responseObserver) {
    }

    @Override
    public void listTaskPushNotificationConfigs(ListTaskPushNotificationConfigsRequest request, StreamObserver<ListTaskPushNotificationConfigsResponse> responseObserver) {
    }

    @Override
    public void deleteTaskPushNotificationConfig(DeleteTaskPushNotificationConfigRequest request, StreamObserver<Empty> responseObserver) {
    }

    */

}
