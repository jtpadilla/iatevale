package io.github.jtpadilla.genai.function;

import com.google.genai.types.FunctionCall;

import java.util.Map;

@FunctionalInterface
public interface FunctionGatewayCall {
    Map<String, Object> execute(FunctionCall functionCall) throws FunctionGatewayException;
}
