package io.github.jtpadilla.product.simpleagent.agentexecutor;

import com.google.lf.a2a.v1.Message;
import com.google.lf.a2a.v1.Part;
import com.google.lf.a2a.v1.Role;
import io.github.jtpadilla.a2a.server.base.lib.spec.A2AError;
import io.github.jtpadilla.a2a.server.base.lib.spec.AgentExecutor;
import io.github.jtpadilla.a2a.server.base.lib.spec.Emitter;
import io.github.jtpadilla.a2a.server.base.lib.spec.RequestContext;

import java.time.LocalDateTime;
import java.util.UUID;

public class SimpleAgentExecutor implements AgentExecutor {

    @Override
    public void execute(RequestContext context, Emitter emitter) throws A2AError {

        final Part part = Part.newBuilder().setText(LocalDateTime.now().toString())
                .build();

        final Message message = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setRole(Role.ROLE_AGENT)
                .addParts(part)
                .build();

        emitter.messageSend(message);

    }

    @Override
    public void cancel(RequestContext context, Emitter emitter) throws A2AError {

    }

}
