package io.github.jtpadilla.a2a.server.grpc.impl;

import io.github.jtpadilla.a2a.server.service.skill.SkillContext;
import io.github.jtpadilla.a2a.server.service.skill.SkillService;
import io.github.jtpadilla.a2a.server.service.skill.spi.SkillProvider;

public class SkillDispatcher {

    final private SkillService skillService;

    public SkillDispatcher(SkillService skillService) {
        this.skillService = skillService;
    }

    private SkillProvider routeTo(SkillContext context) {
        // Hay que implementar un mecanismo de seleccion del skill meiante LLM.
        // Per mientras tanto selecciona el unico que hay..
        return skillService.skillList().getFirst();
    }

    public void dispatch(SkillContext context) {
        routeTo(context).executeSkill(context);
    }

}
