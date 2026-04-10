package io.github.jtpadilla.a2a.server.base.service.skill.spi;

import com.google.lf.a2a.v1.AgentSkill;
import io.helidon.service.registry.Service;

@Service.Contract
public interface SkillProvider {
    AgentSkill getSkillCard();
    void execute(RequestContext context, AgentEmitter agentEmitter) throws A2AError;
    void cancel(RequestContext context, AgentEmitter agentEmitter) throws A2AError;
}
