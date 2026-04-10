package io.github.jtpadilla.a2a.server.base.lib.operations;

import com.google.lf.a2a.v1.*;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.util.logging.Logger;

@Service.Singleton
public class Operations {

    final Logger LOGGER = Logger.getLogger(Operations.class.getName());

    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Operation sendMessage: " + request.getMessage().getMessageId());
    }

    public void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("Operation sendStreamingMessage: " + request.getMessage().getMessageId());
    }

    public void getExtendedAgentCard(GetExtendedAgentCardRequest request, StreamObserver<AgentCard> responseObserver) {
        LOGGER.info("Operation sendStreamingMessage");
    }

}
