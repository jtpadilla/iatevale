package io.github.jtpadilla.a2a.core.vanilla.skill;

import com.google.lf.a2a.v1.AgentSkill;
import io.github.jtpadilla.a2a.core.service.skill.SkillService;
import io.github.jtpadilla.a2a.core.service.skill.spi.SkillProvider;
import io.helidon.service.registry.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service.Singleton
public class VanillaSkillService implements SkillService {

    private static final Logger LOGGER = Logger.getLogger(VanillaSkillService.class.getName());

    private final List<SkillProvider> providers;

    @Service.Inject
    public VanillaSkillService(List<SkillProvider> providers) {

        // Verificacion de que todas las skills tiene un id unico
        Set<String> ids = new HashSet<>();
        for (SkillProvider provider : providers) {
            String id = provider.getSkillCard().getId();
            if (!ids.add(id)) {
                throw new IllegalStateException("Duplicate skill id detected: " + id);
            }
        }

        // Se anotan las skills
        this.providers = providers;

        // Se imprimen las skills instsladas
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
