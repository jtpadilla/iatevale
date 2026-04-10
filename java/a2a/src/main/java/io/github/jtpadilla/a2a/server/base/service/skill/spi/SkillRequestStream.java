package io.github.jtpadilla.a2a.server.base.service.skill.spi;

import com.google.lf.a2a.v1.SendMessageRequest;
import com.google.lf.a2a.v1.StreamResponse;
import io.grpc.stub.StreamObserver;

final public class SkillRequestStream extends SkillRequest {

    final private StreamObserver<StreamResponse> responseObsever;

    public SkillRequestStream(SendMessageRequest request, StreamObserver<StreamResponse> responseObsever) {
        super(request);
        this.responseObsever = responseObsever;
    }

    public StreamObserver<StreamResponse> responseObsever() {
        return responseObsever;
    }

}
