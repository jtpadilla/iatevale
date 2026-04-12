package io.github.jtpadilla.product.simpleagent.agentexecutor;

import io.github.jtpadilla.a2a.server.base.lib.spec.AgentExecutor;
import io.github.jtpadilla.a2a.server.base.service.agentexecutor.AgentEjecutorService;
import io.helidon.service.registry.Service;

@Service.Singleton
public class SimpleAgentExecutorService implements AgentEjecutorService {

    @Override
    public AgentExecutor agentExecutor() {
        return null;
    }

}
