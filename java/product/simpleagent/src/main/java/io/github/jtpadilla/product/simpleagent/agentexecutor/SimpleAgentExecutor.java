package io.github.jtpadilla.product.simpleagent.agentexecutor;

import com.google.lf.a2a.v1.Message;
import com.google.lf.a2a.v1.Part;
import com.google.lf.a2a.v1.Role;
import com.google.lf.a2a.v1.SendMessageRequest;
import io.github.jtpadilla.a2a.server.lib.spec.AgentExecutor;
import io.github.jtpadilla.a2a.server.lib.spec.Emitter;

import java.time.LocalDateTime;
import java.util.UUID;

public class SimpleAgentExecutor implements AgentExecutor {

    @Override
    public void execute(SendMessageRequest sendMessageRequest, Emitter emitter) {

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
    public void cancel(SendMessageRequest sendMessageRequest, Emitter emitter) {

    }

}
