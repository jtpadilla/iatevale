package io.github.jtpadilla.product.genaiexample.function.filterlocations;

import com.google.common.collect.ImmutableMap;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.List;
import java.util.Map;

class Response {

    static public Schema SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .description("Lista de poblaciones que se quieren filtrar")
            .properties(Map.of(
                    "poblaciones", Schema.builder()
                            .type(Type.Known.ARRAY)
                            .items(Schema.builder().type(Type.Known.STRING).build())
                            .description("Array con los nombres de poblaciones en las que estamos interesados")
                            .build()
            ))
            .required("poblaciones")
            .build();


    final private List<String> poblaciones;

    public Response(List<String> poblaciones) {
        this.poblaciones = poblaciones;
    }

    public Map<String, Object> toMap() {
        return ImmutableMap.of("poblaciones", poblaciones);
    }

}
