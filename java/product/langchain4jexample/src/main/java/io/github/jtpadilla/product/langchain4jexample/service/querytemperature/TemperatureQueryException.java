package io.github.jtpadilla.product.langchain4jexample.service.querytemperature;

public class TemperatureQueryException extends Exception {

    public TemperatureQueryException(String message) {
        super(message);
    }

    public TemperatureQueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
