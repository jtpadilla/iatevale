package io.github.jtpadilla.a2a.server.base.lib.spec;

import com.google.lf.a2a.v1.SendMessageRequest;

public interface AgentExecutor {
    void execute(SendMessageRequest sendMessageRequest, Emitter emitter) throws A2AError;
    void cancel(SendMessageRequest sendMessageRequest, Emitter emitter) throws A2AError;
}
