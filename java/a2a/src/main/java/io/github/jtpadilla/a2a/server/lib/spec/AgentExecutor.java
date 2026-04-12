package io.github.jtpadilla.a2a.server.lib.spec;

import com.google.lf.a2a.v1.SendMessageRequest;

public interface AgentExecutor {
    void execute(SendMessageRequest sendMessageRequest, Emitter emitter);
    void cancel(SendMessageRequest sendMessageRequest, Emitter emitter);
}
