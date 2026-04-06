package io.github.jtpadilla.genai.agent;

import io.github.jtpadilla.genai.GenAIWrapperException;

public class AgentException extends GenAIWrapperException {

    public AgentException(String message) {
        super(message);
    }

    public AgentException(String message, Throwable cause) {
        super(message, cause);
    }

}
