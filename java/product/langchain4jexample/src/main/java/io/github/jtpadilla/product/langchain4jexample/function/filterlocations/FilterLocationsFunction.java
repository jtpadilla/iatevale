package io.github.jtpadilla.product.langchain4jexample.function.filterlocations;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import io.github.jtpadilla.langchain4j.function.FunctionGatewayException;

import java.util.List;
import java.util.Map;

public class FilterLocationsFunction {

    public static final String NAME = "filter_locations";

    public static final ToolSpecification SPECIFICATION = ToolSpecification.builder()
            .name(NAME)
            .description("Proporciona la lista de ciudades en la que estamos interesados")
            .parameters(JsonObjectSchema.builder()
                    .description("Lista de poblaciones que se quieren filtrar")
                    .addProperty("poblaciones", JsonArraySchema.builder()
                            .description("Array con los nombres de poblaciones propuestas")
                            .items(new JsonStringSchema())
                            .build())
                    .required(List.of("poblaciones"))
                    .build())
            .build();

    private final List<String> validCities;

    public FilterLocationsFunction(List<String> validCities) {
        this.validCities = validCities;
    }

    public Map<String, Object> execute(ToolExecutionRequest request) throws FunctionGatewayException {
        return execute(Parameter.create(request.arguments())).toMap();
    }

    private Response execute(Parameter parameter) {
        final List<String> filteredList = parameter.poblaciones().stream()
                .filter(this::isValidCity)
                .toList();
        return new Response(filteredList);
    }

    private boolean isValidCity(String candidate) {
        return validCities.stream()
                .map(String::toUpperCase)
                .anyMatch(v -> v.equals(candidate.toUpperCase()));
    }

}
