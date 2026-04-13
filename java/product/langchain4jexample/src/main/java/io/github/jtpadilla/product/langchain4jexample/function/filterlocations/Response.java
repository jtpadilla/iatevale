package io.github.jtpadilla.product.langchain4jexample.function.filterlocations;

import java.util.List;
import java.util.Map;

class Response {

    private final List<String> poblaciones;

    public Response(List<String> poblaciones) {
        this.poblaciones = poblaciones;
    }

    public Map<String, Object> toMap() {
        return Map.of("poblaciones", poblaciones);
    }

}
