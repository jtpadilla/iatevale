package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AgentSkill;

public class AgentSkillVerifier {

    static public AgentSkill verify(AgentSkill msg) {
        return new AgentSkillVerifier(msg).verify();
    }

    final private AgentSkill msg;

    private AgentSkillVerifier(AgentSkill msg) {
        this.msg = msg;
    }

    private AgentSkill getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AgentSkill is null");
        }
        return msg;
    }

    private void verifyId(AgentSkill.Builder builder) {
        if (getThis().getId().isBlank()) {
            throw new IllegalArgumentException("AgentSkill.id is required");
        }
        builder.setId(getThis().getId());
    }

    private void verifyName(AgentSkill.Builder builder) {
        if (getThis().getName().isBlank()) {
            throw new IllegalArgumentException("AgentSkill.name is required");
        }
        builder.setName(getThis().getName());
    }

    private void verifyDescription(AgentSkill.Builder builder) {
        if (getThis().getDescription().isBlank()) {
            throw new IllegalArgumentException("AgentSkill.description is required");
        }
        builder.setDescription(getThis().getDescription());
    }

    private void verifyTags(AgentSkill.Builder builder) {
        if (getThis().getTagsList().isEmpty()) {
            throw new IllegalArgumentException("AgentSkill.tags is required and must not be empty");
        }
        builder.addAllTags(getThis().getTagsList());
    }

    private AgentSkill verify() {
        AgentSkill.Builder builder = AgentSkill.newBuilder();
        verifyId(builder);
        verifyName(builder);
        verifyDescription(builder);
        verifyTags(builder);
        builder.addAllExamples(getThis().getExamplesList());
        builder.addAllInputModes(getThis().getInputModesList());
        builder.addAllOutputModes(getThis().getOutputModesList());
        builder.addAllSecurityRequirements(getThis().getSecurityRequirementsList());
        return builder.build();
    }

}
