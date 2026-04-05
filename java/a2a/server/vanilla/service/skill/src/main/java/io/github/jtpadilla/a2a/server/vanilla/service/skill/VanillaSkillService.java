package io.github.jtpadilla.a2a.server.vanilla.service.skill;

import com.google.lf.a2a.v1.AgentSkill;
import io.github.jtpadilla.a2a.server.service.skill.SkillService;
import io.github.jtpadilla.a2a.server.service.skill.spi.SkillProvider;
import io.helidon.service.registry.Service;

import java.util.List;
import java.util.logging.Logger;

@Service.Singleton
public class VanillaSkillService implements SkillService {

    private static final Logger LOGGER = Logger.getLogger(VanillaSkillService.class.getName());

    private final List<SkillProvider> providers;

    @Service.Inject
    public VanillaSkillService(List<SkillProvider> providers) {
        this.providers = providers;
        LOGGER.info("Skill list:");
        for (SkillProvider provider : providers) {
            final AgentSkill agentSkill = provider.getSkillCard();
            LOGGER.info(String.format("%s (%s): %s", agentSkill.getName(), agentSkill.getId(), agentSkill.getDescription()));
        }
    }

    @Override
    public List<SkillProvider> skillList() {
        return providers;
    }

}
