package io.github.jtpadilla.product.genaiexample.service.querytemperature;

public class TemperatureQueryException extends Exception {

    public TemperatureQueryException(String message) {
        super(message);
    }

    public TemperatureQueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
