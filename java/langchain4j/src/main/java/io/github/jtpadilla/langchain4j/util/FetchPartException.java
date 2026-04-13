package io.github.jtpadilla.langchain4j.util;

import io.github.jtpadilla.langchain4j.Lc4jException;

public class FetchPartException extends Lc4jException {

    public FetchPartException(String message) {
        super(message);
    }

    public FetchPartException(String message, Throwable cause) {
        super(message, cause);
    }

}
