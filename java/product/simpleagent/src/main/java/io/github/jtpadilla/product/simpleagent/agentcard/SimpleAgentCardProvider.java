package io.github.jtpadilla.product.simpleagent.agentcard;

import com.google.lf.a2a.v1.AgentCapabilities;
import com.google.lf.a2a.v1.AgentCard;
import com.google.lf.a2a.v1.AgentInterface;
import io.github.jtpadilla.a2a.server.base.provider.agentcard.AgentCardProvider;
import io.helidon.service.registry.Service;

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
        return AgentCard.newBuilder()
                .setName("IAtevale Agent")
                .setDescription("Pemite evaluar la tecnologia A2A")
                .setVersion("0.0.1")
                .addSupportedInterfaces(AgentInterface.newBuilder()
                        .setUrl("http://localhost:8080/lf.a2a.v1.A2AService/GetTask")
                        .setProtocolBinding("GRPC")
                        .setProtocolVersion("1.0")
                        .build())
                .setCapabilities(AgentCapabilities.newBuilder()
                        .setStreaming(false)
                        .build())
                .addDefaultInputModes("text/plain")
                .addDefaultOutputModes("text/plain");
    }

}
