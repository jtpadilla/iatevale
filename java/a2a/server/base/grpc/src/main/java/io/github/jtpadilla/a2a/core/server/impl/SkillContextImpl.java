package io.github.jtpadilla.a2a.core.server.impl;

import com.google.lf.a2a.v1.SendMessageRequest;
import com.google.lf.a2a.v1.SendMessageResponse;
import com.google.lf.a2a.v1.StreamResponse;
import io.github.jtpadilla.a2a.server.service.skill.SkillContext;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequest;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequestSimple;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequestStream;
import io.grpc.stub.StreamObserver;

public class SkillContextImpl implements SkillContext {

    public static SkillContextImpl forSimple(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        return new SkillContextImpl(new SkillRequestSimple(request, responseObserver));
    }

    public static SkillContextImpl forStream(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        return new SkillContextImpl(new SkillRequestStream(request, responseObserver));
    }

    final private SkillRequest request;

    private SkillContextImpl(SkillRequest request) {
        this.request = request;
    }

    @Override
    public SkillRequest request() {
        return request;
    }

}
