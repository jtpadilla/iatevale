package io.github.jtpadilla.langchain4j.agent;

import dev.langchain4j.model.chat.ChatModel;
import io.github.jtpadilla.langchain4j.Lc4jService;

/**
 * Recursos compartidos entre agentes: servicio LLM y modelo preconfigurado.
 * Equivalente a {@code AgentResources(Client, String)} de la librería google-genai;
 * aquí {@link ChatModel} reemplaza al {@code Client} (que era AutoCloseable),
 * y {@link Lc4jService} permite crear modelos con distintas configuraciones si es necesario.
 */
public record AgentResources(Lc4jService service, ChatModel model, String modelName) {

    /**
     * Constructor de conveniencia: crea el modelo estándar a partir del servicio.
     */
    public static AgentResources of(Lc4jService service) {
        return new AgentResources(service, service.createModel(), service.getModelName());
    }

}
