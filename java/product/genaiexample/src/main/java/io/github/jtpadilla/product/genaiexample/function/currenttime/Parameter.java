package io.github.jtpadilla.product.genaiexample.function.currenttime;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import io.github.jtpadilla.genai.function.FunctionGatewayException;

import java.util.Map;

class Parameter {

    static public Schema SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of()).
            build();

    static public Parameter create(Map<String, Object> args) throws FunctionGatewayException {
        return new Parameter();
    }

    private Parameter() {
    }

}
