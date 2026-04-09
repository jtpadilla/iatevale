package io.github.jtpadilla.a2a.core.service.skill.spi;

import com.google.lf.a2a.v1.AgentSkill;
import io.github.jtpadilla.a2a.core.service.skill.SkillContext;
import io.helidon.service.registry.Service;

@Service.Contract
public interface SkillProvider {
    AgentSkill getSkillCard();

    void executeSkill(SkillContext context);
}
