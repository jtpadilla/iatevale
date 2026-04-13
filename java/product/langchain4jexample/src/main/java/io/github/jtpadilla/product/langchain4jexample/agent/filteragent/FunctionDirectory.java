package io.github.jtpadilla.product.langchain4jexample.agent.filteragent;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import io.github.jtpadilla.langchain4j.function.FunctionGatewayException;
import io.github.jtpadilla.product.langchain4jexample.function.currenttime.GetCurrentTimeFunction;
import io.github.jtpadilla.product.langchain4jexample.function.filterlocations.FilterLocationsFunction;

import java.util.List;
import java.util.Map;

/**
 * Directorio de herramientas disponibles para el {@link FilterAgent}.
 * Equivalente a {@code FunctionDirectory} de genaiexample; aquí cada herramienta
 * se registra como {@link ToolSpecification} en vez de {@code FunctionDeclaration},
 * y el dispatch se hace sobre {@link ToolExecutionRequest} en vez de {@code FunctionCall}.
 */
class FunctionDirectory {

    public static List<ToolSpecification> specifications() {
        return List.of(
                GetCurrentTimeFunction.SPECIFICATION,
                FilterLocationsFunction.SPECIFICATION
        );
    }

    private final FilterLocationsFunction filterLocationsFunction;

    FunctionDirectory(List<String> validCities) {
        this.filterLocationsFunction = new FilterLocationsFunction(validCities);
    }

    public Map<String, Object> executeTool(ToolExecutionRequest request) throws FunctionGatewayException {
        return switch (request.name()) {
            case GetCurrentTimeFunction.NAME -> GetCurrentTimeFunction.execute(request);
            case FilterLocationsFunction.NAME -> filterLocationsFunction.execute(request);
            default -> throw new FunctionGatewayException("Función no reconocida: " + request.name());
        };
    }

}
