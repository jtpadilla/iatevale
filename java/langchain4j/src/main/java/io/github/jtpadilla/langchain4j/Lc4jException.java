package io.github.jtpadilla.langchain4j;

public class Lc4jException extends Exception {

    public Lc4jException(String message) {
        super(message);
    }

    public Lc4jException(String message, Throwable cause) {
        super(message, cause);
    }

}
