package io.github.jtpadilla.a2a.server.base.lib.operations;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.base.provider.agentexecutor.AgentEjecutorProvider;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.logging.Logger;

@Service.Singleton
public class Operations {

    final Logger LOGGER = Logger.getLogger(Operations.class.getName());

    final private AgentEjecutorProvider agentEjecutorProvider;

    @Service.Inject
    public Operations(AgentEjecutorProvider agentEjecutorProvider) {
        this.agentEjecutorProvider = agentEjecutorProvider;
    }

    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Operation sendMessage: " + request.getMessage().getMessageId());
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
