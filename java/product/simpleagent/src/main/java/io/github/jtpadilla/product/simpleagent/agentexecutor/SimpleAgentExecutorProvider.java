package io.github.jtpadilla.product.simpleagent.agentexecutor;

import io.github.jtpadilla.a2a.server.lib.spec.AgentExecutor;
import io.github.jtpadilla.a2a.server.provider.agentexecutor.AgentEjecutorProvider;
import io.helidon.service.registry.Service;

@Service.Singleton
public class SimpleAgentExecutorProvider implements AgentEjecutorProvider {

    @Override
    public AgentExecutor agentExecutor() {
        return new SimpleAgentExecutor();
    }

}
