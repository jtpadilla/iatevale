package io.github.jtpadilla.product.genaiexample.function.filterlocations;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import io.github.jtpadilla.genai.function.FunctionGatewayException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class Parameter {

    static public Schema SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .description("Lista de poblaciones que se quieren filtrar")
            .properties(Map.of(
                    "poblaciones", Schema.builder()
                            .type(Type.Known.ARRAY)
                            .items(Schema.builder().type(Type.Known.STRING).build())
                            .description("Array con los nombres de poblaciones propuestas")
                            .build()
            ))
            .required("poblaciones")
            .build();


    static public Parameter create(Map<String, Object> args) throws FunctionGatewayException {
        final Builder builder = builder(createPoblaciones(args));
        return builder.build();
    }

    static private List<String> createPoblaciones(Map<String, Object> args) throws FunctionGatewayException {
        final Optional<Object> object = Optional.ofNullable(args.get("poblaciones"));
        if (object.isEmpty()) {
            return new ArrayList<>();
        }
        if (object.get() instanceof List<?> numbers) {
            if (numbers.stream().anyMatch(n -> !(n instanceof String))) {
                throw new FunctionGatewayException("FilterLocations.Paraneters.poblaciones must be String type");
            }
            return numbers.stream()
                    .map(n -> (String) n)
                    .collect(Collectors.toList());
        } else {
            throw new FunctionGatewayException("MathCalculatorFunction.Parameters.poblaciones not is Array<String>");
        }
    }

    static private Builder builder(List<String> poblaciones) {
        return new Builder(poblaciones);
    }

    static private class Builder {

        final List<String> poblaciones;

        private Builder(List<String> poblaciones) {
            this.poblaciones = poblaciones;
        }

        public Parameter build() {
            return new Parameter(poblaciones);
        }

    }

    final private List<String> poblaciones;

    private Parameter(List<String> poblaciones) {
        this.poblaciones = poblaciones;
    }

    public List<String> poblaciones() {
        return poblaciones;
    }

}
