package io.github.jtpadilla.a2a.core.server;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.core.server.impl.SkillContextImpl;
import io.github.jtpadilla.a2a.core.server.impl.SkillDispatcher;
import io.github.jtpadilla.a2a.server.service.agentcard.AgentCardService;
import io.github.jtpadilla.a2a.server.service.skill.SkillService;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service.Singleton
public class A2AService extends A2AServiceGrpc.A2AServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(A2AService.class.getName());

    final private SkillDispatcher skillDispatcher;
    final private AgentCardService agentCardService;

    @Service.Inject
    public A2AService(SkillService skillService, AgentCardService agentCardService) {
        this.skillDispatcher = new SkillDispatcher(skillService);
        this.agentCardService = agentCardService;
    }

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Received sendMessage: " + request.getMessage().getMessageId());
        skillDispatcher.dispatch(SkillContextImpl.forSimple(request, responseObserver));
    }

    @Override
    public void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("Received sendStreamingMessage: " + request.getMessage().getMessageId());
        skillDispatcher.dispatch(SkillContextImpl.forStream(request, responseObserver));
    }

    @Override
    public void getExtendedAgentCard(GetExtendedAgentCardRequest request, StreamObserver<AgentCard> responseObserver) {
        responseObserver.onNext(agentCardService.agentCard());
        responseObserver.onCompleted();
    }
}
