package io.github.jtpadilla.genai.schema;

import io.github.jtpadilla.genai.GenAIWrapperException;

public class SchemaException extends GenAIWrapperException {

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, Throwable cause) {
        super(message, cause);
    }

}
