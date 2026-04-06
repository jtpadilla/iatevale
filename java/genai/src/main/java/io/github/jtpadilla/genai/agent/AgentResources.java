package io.github.jtpadilla.genai.agent;

import com.google.genai.Client;

public record AgentResources(Client client, String llmModel) {
}
