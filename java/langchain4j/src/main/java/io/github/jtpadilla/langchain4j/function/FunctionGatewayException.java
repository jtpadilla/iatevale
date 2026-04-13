package io.github.jtpadilla.langchain4j.function;

import io.github.jtpadilla.langchain4j.Lc4jException;

public class FunctionGatewayException extends Lc4jException {

    public FunctionGatewayException(String message) {
        super(message);
    }

    public FunctionGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

}
