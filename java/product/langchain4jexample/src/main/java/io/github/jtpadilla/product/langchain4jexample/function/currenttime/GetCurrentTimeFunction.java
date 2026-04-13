package io.github.jtpadilla.product.langchain4jexample.function.currenttime;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;

import java.time.LocalDateTime;
import java.util.Map;

public class GetCurrentTimeFunction {

    public static final String NAME = "get_current_time";

    public static final ToolSpecification SPECIFICATION = ToolSpecification.builder()
            .name(NAME)
            .description("Obtiene la fecha y hora actual sin información de zona horaria")
            .parameters(JsonObjectSchema.builder().build())
            .build();

    public static Map<String, Object> execute(ToolExecutionRequest request) {
        return execute(Parameter.create()).toMap();
    }

    private static Response execute(Parameter parameter) {
        return new Response(LocalDateTime.now());
    }

}
