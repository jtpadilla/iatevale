package io.github.jtpadilla.product.simpleagent.agentcard;

import com.google.lf.a2a.v1.AgentCapabilities;
import com.google.lf.a2a.v1.AgentCard;
import com.google.lf.a2a.v1.AgentInterface;
import com.google.lf.a2a.v1.AgentSkill;
import io.github.jtpadilla.a2a.server.base.provider.agentcard.AgentCardProvider;
import io.helidon.service.registry.Service;

import java.util.UUID;

@Service.Singleton
public class SimpleAgentCardProvider implements AgentCardProvider {

    final private AgentCard agentCard;

    public SimpleAgentCardProvider() {
        this.agentCard = agentCardBuilder().build();
    }

    @Override
    public AgentCard agentCard() {
        return agentCard;
    }

    private AgentCard.Builder agentCardBuilder() {

        final AgentInterface agentInterface = AgentInterface.newBuilder()
                .setUrl("http://localhost:8080/lf.a2a.v1.A2AService")
                .setProtocolBinding("GRPC")
                .setProtocolVersion("1.0")
                .build();

        final AgentCapabilities agentCapabilities = AgentCapabilities.newBuilder()
                .setStreaming(false)
                .setPushNotifications(false)
                .setExtendedAgentCard(false)
                .build();

        final AgentSkill nowSkill = AgentSkill.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Current date and time")
                .setDescription("Current data and time without timezone")
                .addTags("time")
                .addTags("date")
                .addTags("datetime")
                .addTags("now")
                .build();

        return AgentCard.newBuilder()
                .setName("IAtevale Agent")
                .setDescription("Pemite evaluar la tecnologia A2A")
                .setVersion("0.0.1")
                .addSupportedInterfaces(agentInterface)
                .setCapabilities(agentCapabilities)
                .addDefaultInputModes("text/plain")
                .addDefaultOutputModes("text/plain")
                .addSkills(nowSkill);

    }

}
