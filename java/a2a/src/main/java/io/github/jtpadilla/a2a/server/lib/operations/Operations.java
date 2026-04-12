package io.github.jtpadilla.a2a.server.lib.operations;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.common.verifier.A2AVerifier;
import io.github.jtpadilla.a2a.server.lib.operations.executor.impl.EmitterImpl;
import io.github.jtpadilla.a2a.server.lib.spec.AgentExecutor;
import io.github.jtpadilla.a2a.server.lib.spec.Emitter;
import io.github.jtpadilla.a2a.server.provider.agentexecutor.AgentEjecutorProvider;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.logging.Logger;

@Service.Singleton
public class Operations {

    final Logger LOGGER = Logger.getLogger(Operations.class.getName());

    final private AgentExecutor agentExecutor;

    @Service.Inject
    public Operations(AgentEjecutorProvider agentEjecutorProvider) {
        this.agentExecutor = agentEjecutorProvider.agentExecutor();
    }

    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Operation sendMessage: " + request.getMessage().getMessageId());
        final SendMessageRequest validatedRequest = A2AVerifier.verify(request);
        final Emitter emitter = new EmitterImpl(null, validatedRequest);
        agentExecutor.execute(validatedRequest, emitter);
        responseObserver.onError(Status.UNIMPLEMENTED.asRuntimeException());
    }

    public void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("Operation sendStreamingMessage: " + request.getMessage().getMessageId());
        responseObserver.onError(Status.UNIMPLEMENTED.asRuntimeException());
    }

    public void getExtendedAgentCard(GetExtendedAgentCardRequest request, StreamObserver<AgentCard> responseObserver) {
        LOGGER.info("Operation getExtendedAgentCard");
        responseObserver.onError(Status.UNIMPLEMENTED.asRuntimeException());
    }

}
