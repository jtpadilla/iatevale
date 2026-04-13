package io.github.jtpadilla.langchain4j.agent;

import io.github.jtpadilla.langchain4j.Lc4jException;

public class AgentException extends Lc4jException {

    public AgentException(String message) {
        super(message);
    }

    public AgentException(String message, Throwable cause) {
        super(message, cause);
    }

}
