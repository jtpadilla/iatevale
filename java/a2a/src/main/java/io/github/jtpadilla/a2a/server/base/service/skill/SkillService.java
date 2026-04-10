package io.github.jtpadilla.a2a.server.base.service.skill;

import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillProvider;

import java.util.List;

public interface SkillService {
    List<SkillProvider> skillList();
}
