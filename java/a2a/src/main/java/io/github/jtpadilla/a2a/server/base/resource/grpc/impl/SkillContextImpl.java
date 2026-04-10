package io.github.jtpadilla.a2a.server.base.resource.grpc.impl;

import com.google.lf.a2a.v1.SendMessageRequest;
import com.google.lf.a2a.v1.SendMessageResponse;
import com.google.lf.a2a.v1.StreamResponse;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillContext;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillRequest;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillRequestSimple;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillRequestStream;
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
