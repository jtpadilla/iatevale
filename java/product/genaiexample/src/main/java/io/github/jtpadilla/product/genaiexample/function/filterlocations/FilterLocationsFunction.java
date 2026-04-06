package io.github.jtpadilla.product.genaiexample.function.filterlocations;

import com.google.genai.types.FunctionDeclaration;
import io.github.jtpadilla.genai.function.FunctionGatewayException;

import java.util.List;
import java.util.Map;

public class FilterLocationsFunction {

    static public final String NAME = "filter_locations";

    static public final FunctionDeclaration DECLARATION = FunctionDeclaration.builder()
            .name(NAME)
            .description("Proporciona la lista de ciudades en la que estamos interesados")
            .parameters(Parameter.SCHEMA)
            .response(Response.SCHEMA)
            .build();

    final private List<String> validCities;

    public FilterLocationsFunction(List<String> validCities) {
        this.validCities = validCities;
    }

    public Map<String, Object> execute(Map<String, Object> args) throws FunctionGatewayException {
        return execute(Parameter.create(args)).toMap();
    }

    private Response execute(Parameter parameter) {
        final List<String> filteredList = parameter.poblaciones().stream()
                .filter(this::isValidCity)
                .toList();
        return new Response(filteredList);
    }

    private boolean isValidCity(String required) {
        return validCities.stream()
                .map(String::toUpperCase)
                .anyMatch((query)->query.equals(required.toUpperCase()));
    }

}
