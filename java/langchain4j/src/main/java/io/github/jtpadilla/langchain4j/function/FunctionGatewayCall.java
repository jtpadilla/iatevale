package io.github.jtpadilla.langchain4j.function;

import dev.langchain4j.agent.tool.ToolExecutionRequest;

import java.util.Map;

/**
 * Implementación de una herramienta invocada por el LLM.
 * Equivalente a {@code FunctionGatewayCall} de la librería google-genai,
 * donde {@link ToolExecutionRequest} reemplaza a {@code FunctionCall}.
 *
 * <p>Recibe la petición de invocación del LLM (nombre + argumentos JSON)
 * y devuelve el resultado como mapa de clave-valor que será serializado a JSON.
 */
@FunctionalInterface
public interface FunctionGatewayCall {
    Map<String, Object> execute(ToolExecutionRequest request) throws FunctionGatewayException;
}
