package io.github.jtpadilla.langchain4j.function.part;

import dev.langchain4j.agent.tool.ToolExecutionRequest;

/**
 * Envuelve una {@link ToolExecutionRequest} del LLM.
 * Equivalente a {@code PartFunctionCallWrapper} de la librería google-genai.
 */
public final class PartToolCallWrapper extends PartWrapper {

    private final ToolExecutionRequest request;

    public PartToolCallWrapper(ToolExecutionRequest request) {
        this.request = request;
    }

    public ToolExecutionRequest request() {
        return request;
    }

    public String name() {
        return request.name();
    }

    /**
     * Argumentos de la herramienta tal y como los envía el LLM: string JSON.
     * Equivalente al mapa {@code args()} del original; usar Gson para deserializar si es necesario.
     */
    public String argumentsJson() {
        return request.arguments();
    }

    @Override
    public String toString() {
        return "PartToolCallWrapper{name='" + request.name() + "', arguments=" + request.arguments() + "}";
    }

}
