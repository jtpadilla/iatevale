package io.github.jtpadilla.a2a.skill.echo;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.A2AError;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.AgentEmitter;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.RequestContext;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillProvider;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service.Singleton
public class EchoSkill implements SkillProvider {

    private static final Logger LOGGER = Logger.getLogger(EchoSkill.class.getName());

    @Override
    public AgentSkill getSkillCard() {
        return AgentSkill.newBuilder()
                .setId("079bd671-8f74-4638-8b88-5d87e7159797")
                .setName("Echo")
                .setDescription("Echoes input")
                .addTags("echo")
                .build();
    }

    @Override
    public void execute(RequestContext context, AgentEmitter agentEmitter) throws A2AError {
//            sendMessage(simple.request(), simple.responseObserver());
    }

    @Override
    public void cancel(RequestContext context, AgentEmitter agentEmitter) throws A2AError {

    }

    private String textFromRequest(SendMessageRequest request) {
        return request.getMessage().getPartsList().stream()
                .filter(Part::hasText)
                .map(Part::getText)
                .findFirst()
                .orElse("");
    }

    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {

        LOGGER.info("Received sendMessage: " + request.getMessage().getMessageId());

        String text = textFromRequest(request);

        Message echoMessage = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setContextId(request.getMessage().getContextId())
                .setRole(Role.ROLE_AGENT)
                .addParts(Part.newBuilder().setText(text).build())
                .build();

        responseObserver.onNext(SendMessageResponse.newBuilder()
                .setMessage(echoMessage)
                .build());
        responseObserver.onCompleted();

    }

}
