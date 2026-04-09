package io.github.jtpadilla.a2a.core.service.skill;

import io.github.jtpadilla.a2a.core.service.skill.spi.SkillProvider;

import java.util.List;

public interface SkillService {
    List<SkillProvider> skillList();
}
