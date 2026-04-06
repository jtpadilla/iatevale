package io.github.jtpadilla.product.genaiexample.function.currenttime;

import com.google.common.collect.ImmutableMap;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.time.LocalDateTime;
import java.util.Map;

class Response {

    static public Schema SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .description("Fecha y hora actual")
            .properties(ImmutableMap.of(
                    "current_time", Schema.builder()
                            .type(Type.Known.STRING)
                            .description("Fecha y hora actual en formato ISO-8601")
                            .build()))
            .required("current_time")
            .build();

    final private String currentTime;

    public Response(LocalDateTime currentTime) {
        this.currentTime = currentTime.toString();
    }

    public Map<String, Object> toMap() {
        return ImmutableMap.of("current_time", currentTime);
    }

}
