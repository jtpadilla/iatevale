package io.github.jtpadilla.langchain4j.schema;

import io.github.jtpadilla.langchain4j.Lc4jException;

public class SchemaException extends Lc4jException {

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, Throwable cause) {
        super(message, cause);
    }

}
