package io.github.jtpadilla.a2a.server.base.lib.spec;

public interface AgentExecutor {
    void execute(RequestContext context, Emitter emitter) throws A2AError;
    void cancel(RequestContext context, Emitter emitter) throws A2AError;
}
