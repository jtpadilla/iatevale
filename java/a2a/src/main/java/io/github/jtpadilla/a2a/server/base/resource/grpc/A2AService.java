package io.github.jtpadilla.a2a.server.base.resource.grpc;

import com.google.lf.a2a.v1.*;
import com.google.protobuf.Empty;
import io.github.jtpadilla.a2a.server.base.service.agentcard.AgentCardService;
import io.github.jtpadilla.a2a.server.base.service.skill.SkillService;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.logging.Logger;

@Service.Singleton
public class A2AService extends A2AServiceGrpc.A2AServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(A2AService.class.getName());

    final private AgentCardService agentCardService;

    @Service.Inject
    public A2AService(SkillService skillService, AgentCardService agentCardService) {
        this.agentCardService = agentCardService;
    }

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Received sendMessage: " + request.getMessage().getMessageId());
    }

    @Override
    public void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("Received sendStreamingMessage: " + request.getMessage().getMessageId());
    }

    @Override
    public void getExtendedAgentCard(GetExtendedAgentCardRequest request, StreamObserver<AgentCard> responseObserver) {
        responseObserver.onNext(agentCardService.agentCard());
        responseObserver.onCompleted();
    }

    @Override
    public void getTask(GetTaskRequest request, StreamObserver<Task> responseObserver) {
        super.getTask(request, responseObserver);
    }

    @Override
    public void listTasks(ListTasksRequest request, StreamObserver<ListTasksResponse> responseObserver) {
        super.listTasks(request, responseObserver);
    }

    @Override
    public void cancelTask(CancelTaskRequest request, StreamObserver<Task> responseObserver) {
        super.cancelTask(request, responseObserver);
    }

    @Override
    public void subscribeToTask(SubscribeToTaskRequest request, StreamObserver<StreamResponse> responseObserver) {
        super.subscribeToTask(request, responseObserver);
    }

    @Override
    public void createTaskPushNotificationConfig(CreateTaskPushNotificationConfigRequest request, StreamObserver<TaskPushNotificationConfig> responseObserver) {
        super.createTaskPushNotificationConfig(request, responseObserver);
    }

    @Override
    public void getTaskPushNotificationConfig(GetTaskPushNotificationConfigRequest request, StreamObserver<TaskPushNotificationConfig> responseObserver) {
        super.getTaskPushNotificationConfig(request, responseObserver);
    }

    @Override
    public void listTaskPushNotificationConfigs(ListTaskPushNotificationConfigsRequest request, StreamObserver<ListTaskPushNotificationConfigsResponse> responseObserver) {
        super.listTaskPushNotificationConfigs(request, responseObserver);
    }

    @Override
    public void deleteTaskPushNotificationConfig(DeleteTaskPushNotificationConfigRequest request, StreamObserver<Empty> responseObserver) {
        super.deleteTaskPushNotificationConfig(request, responseObserver);
    }

}
