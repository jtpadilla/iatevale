package io.github.jtpadilla.langchain4j.generate;

import io.github.jtpadilla.langchain4j.Lc4jException;

public final class GenerateException extends Lc4jException {

    public GenerateException(String message) {
        super(message);
    }

    public GenerateException(String message, Throwable cause) {
        super(message, cause);
    }

}
