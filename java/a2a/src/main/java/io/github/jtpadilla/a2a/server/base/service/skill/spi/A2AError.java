package io.github.jtpadilla.a2a.server.base.service.skill.spi;

public class A2AError extends Exception {

    public A2AError() {
    }

    public A2AError(String message) {
        super(message);
    }

    public A2AError(String message, Throwable cause) {
        super(message, cause);
    }
    
}
