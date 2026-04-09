package io.github.jtpadilla.product.genaiexample.agent.filteragent;

import com.google.genai.types.FunctionCall;
import com.google.genai.types.Tool;
import io.github.jtpadilla.genai.function.FunctionGatewayException;
import io.github.jtpadilla.product.genaiexample.function.currenttime.GetCurrentTimeFunction;
import io.github.jtpadilla.product.genaiexample.function.filterlocations.FilterLocationsFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FunctionDirectory {

    public static Tool createTool() {
        return Tool.builder()
                .functionDeclarations(
                        GetCurrentTimeFunction.DECLARATION,
                        FilterLocationsFunction.DECLARATION
                )
                .build();
    }

    final private FilterLocationsFunction filterLocationsFunction;

    FunctionDirectory(List<String> validCities) {
        this.filterLocationsFunction = new FilterLocationsFunction(validCities);
    }

    public Map<String, Object> executeTool(FunctionCall functionCall) throws FunctionGatewayException {
        if (functionCall.name().isEmpty()) {
            throw new FunctionGatewayException("No se ha proporcionado el nombre de la función");
        }
        final String functionName = functionCall.name().get();
        final Map<String, Object> args = functionCall.args().orElse(new HashMap<>());

        return switch (functionName) {
            case GetCurrentTimeFunction.NAME -> GetCurrentTimeFunction.execute(args);
            case FilterLocationsFunction.NAME -> filterLocationsFunction.execute(args);
            default -> throw new FunctionGatewayException("Función no reconocida: " + functionName);
        };
    }

}
