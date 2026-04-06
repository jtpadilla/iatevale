package io.github.jtpadilla.a2a.skill.datetime;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.service.skill.SkillContext;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequestSimple;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequestStream;
import io.github.jtpadilla.a2a.server.service.skill.spi.SkillProvider;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

@Service.Singleton
public class DateTimeSkill implements SkillProvider {

    private static final Logger LOGGER = Logger.getLogger(DateTimeSkill.class.getName());

    @Override
    public AgentSkill getSkillCard() {
        return AgentSkill.newBuilder()
                .setId("b3a1c2d4-e5f6-7890-abcd-ef1234567890")
                .setName("DateTime")
                .setDescription("Returns current UTC date and time in ISO 8601 format")
                .addTags("datetime")
                .addTags("time")
                .addTags("date")
                .build();
    }

    @Override
    public void executeSkill(SkillContext context) {
        switch (context.request()) {
            case SkillRequestSimple simple -> sendMessage(simple.request(), simple.responseObserver());
            case SkillRequestStream stream -> sendStreamingMessage(stream.request(), stream.responseObsever());
        }
    }

    private String currentDateTimeUtc() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    private void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Received sendMessage: " + request.getMessage().getMessageId());

        Message responseMessage = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setContextId(request.getMessage().getContextId())
                .setRole(Role.ROLE_AGENT)
                .addParts(Part.newBuilder().setText(currentDateTimeUtc()).build())
                .build();

        responseObserver.onNext(SendMessageResponse.newBuilder()
                .setMessage(responseMessage)
                .build());
        responseObserver.onCompleted();
    }

    private void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("Received sendStreamingMessage: " + request.getMessage().getMessageId());

        responseObserver.onNext(StreamResponse.newBuilder()
                .setStatusUpdate(TaskStatusUpdateEvent.newBuilder()
                        .setContextId(request.getMessage().getContextId())
                        .setStatus(TaskStatus.newBuilder()
                                .setState(TaskState.TASK_STATE_WORKING)
                                .build())
                        .build())
                .build());

        Message responseMessage = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setContextId(request.getMessage().getContextId())
                .setRole(Role.ROLE_AGENT)
                .addParts(Part.newBuilder().setText(currentDateTimeUtc()).build())
                .build();

        responseObserver.onNext(StreamResponse.newBuilder()
                .setMessage(responseMessage)
                .build());
        responseObserver.onCompleted();
    }
}
