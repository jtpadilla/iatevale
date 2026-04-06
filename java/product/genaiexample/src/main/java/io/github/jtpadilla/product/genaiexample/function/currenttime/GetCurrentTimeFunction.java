package io.github.jtpadilla.product.genaiexample.function.currenttime;

import com.google.genai.types.FunctionDeclaration;
import io.github.jtpadilla.genai.function.FunctionGatewayException;

import java.time.LocalDateTime;
import java.util.Map;

public class GetCurrentTimeFunction {

    static public final String NAME = "get_current_time";

    static public final FunctionDeclaration DECLARATION = FunctionDeclaration.builder()
            .name(NAME)
            .description("Obtiene la fecha y hora actual sin información de zona horaria")
            .parameters(Parameter.SCHEMA)
            .response(Response.SCHEMA)
            .build();

    public static Map<String, Object> execute(Map<String, Object> args) throws FunctionGatewayException {
        return execute(Parameter.create(args)).toMap();
    }

    private static Response execute(Parameter parameters) throws FunctionGatewayException {
        // Código de la función
        final LocalDateTime now = LocalDateTime.now();
        // respuesta a la función
        return new Response(now);
    }

}
