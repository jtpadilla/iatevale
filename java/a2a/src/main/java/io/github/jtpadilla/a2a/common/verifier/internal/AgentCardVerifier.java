package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.AgentCard;

public class AgentCardVerifier {

    static public AgentCard verify(AgentCard msg) {
        return new AgentCardVerifier(msg).verify();
    }

    final private AgentCard msg;

    private AgentCardVerifier(AgentCard msg) {
        this.msg = msg;
    }

    private AgentCard getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("AgentCard is null");
        }
        return msg;
    }

    private void verifyName(AgentCard.Builder builder) {
        if (getThis().getName().isBlank()) {
            throw new IllegalArgumentException("AgentCard.name is required");
        }
        builder.setName(getThis().getName());
    }

    private void verifyDescription(AgentCard.Builder builder) {
        if (getThis().getDescription().isBlank()) {
            throw new IllegalArgumentException("AgentCard.description is required");
        }
        builder.setDescription(getThis().getDescription());
    }

    private void verifySupportedInterfaces(AgentCard.Builder builder) {
        if (getThis().getSupportedInterfacesList().isEmpty()) {
            throw new IllegalArgumentException("AgentCard.supported_interfaces is required and must not be empty");
        }
        builder.addAllSupportedInterfaces(getThis().getSupportedInterfacesList());
    }

    private void verifyVersion(AgentCard.Builder builder) {
        if (getThis().getVersion().isBlank()) {
            throw new IllegalArgumentException("AgentCard.version is required");
        }
        builder.setVersion(getThis().getVersion());
    }

    private void verifyCapabilities(AgentCard.Builder builder) {
        if (!getThis().hasCapabilities()) {
            throw new IllegalArgumentException("AgentCard.capabilities is required");
        }
        builder.setCapabilities(getThis().getCapabilities());
    }

    private void verifyDefaultInputModes(AgentCard.Builder builder) {
        if (getThis().getDefaultInputModesList().isEmpty()) {
            throw new IllegalArgumentException("AgentCard.default_input_modes is required and must not be empty");
        }
        builder.addAllDefaultInputModes(getThis().getDefaultInputModesList());
    }

    private void verifyDefaultOutputModes(AgentCard.Builder builder) {
        if (getThis().getDefaultOutputModesList().isEmpty()) {
            throw new IllegalArgumentException("AgentCard.default_output_modes is required and must not be empty");
        }
        builder.addAllDefaultOutputModes(getThis().getDefaultOutputModesList());
    }

    private void verifySkills(AgentCard.Builder builder) {
        if (getThis().getSkillsList().isEmpty()) {
            throw new IllegalArgumentException("AgentCard.skills is required and must not be empty");
        }
        builder.addAllSkills(getThis().getSkillsList());
    }

    private AgentCard verify() {
        AgentCard.Builder builder = AgentCard.newBuilder();
        verifyName(builder);
        verifyDescription(builder);
        verifySupportedInterfaces(builder);
        verifyVersion(builder);
        verifyCapabilities(builder);
        verifyDefaultInputModes(builder);
        verifyDefaultOutputModes(builder);
        verifySkills(builder);
        if (getThis().hasProvider()) {
            builder.setProvider(getThis().getProvider());
        }
        if (getThis().hasDocumentationUrl()) {
            builder.setDocumentationUrl(getThis().getDocumentationUrl());
        }
        builder.putAllSecuritySchemes(getThis().getSecuritySchemesMap());
        builder.addAllSecurityRequirements(getThis().getSecurityRequirementsList());
        builder.addAllSignatures(getThis().getSignaturesList());
        if (getThis().hasIconUrl()) {
            builder.setIconUrl(getThis().getIconUrl());
        }
        return builder.build();
    }

}
