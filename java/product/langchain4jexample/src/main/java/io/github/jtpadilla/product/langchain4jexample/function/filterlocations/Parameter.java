package io.github.jtpadilla.product.langchain4jexample.function.filterlocations;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.jtpadilla.langchain4j.function.FunctionGatewayException;

import java.util.List;

/**
 * Parámetros de entrada para filter_locations.
 * En LangChain4j los argumentos llegan como string JSON desde {@code ToolExecutionRequest.arguments()},
 * y se deserializan con Gson en vez de iterar un {@code Map<String, Object>}.
 */
class Parameter {

    private static final Gson GSON = new Gson();

    /** Record interno para la deserialización Gson. */
    private record Args(List<String> poblaciones) {}

    static Parameter create(String argumentsJson) throws FunctionGatewayException {
        try {
            final Args args = GSON.fromJson(argumentsJson, Args.class);
            final List<String> poblaciones = (args != null && args.poblaciones() != null)
                    ? args.poblaciones()
                    : List.of();
            return new Parameter(poblaciones);
        } catch (JsonSyntaxException e) {
            throw new FunctionGatewayException("filter_locations: invalid JSON arguments: " + argumentsJson, e);
        }
    }

    private final List<String> poblaciones;

    private Parameter(List<String> poblaciones) {
        this.poblaciones = poblaciones;
    }

    public List<String> poblaciones() {
        return poblaciones;
    }

}
