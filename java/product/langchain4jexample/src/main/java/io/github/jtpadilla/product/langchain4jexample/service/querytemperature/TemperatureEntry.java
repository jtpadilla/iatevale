package io.github.jtpadilla.product.langchain4jexample.service.querytemperature;

import java.time.LocalDateTime;

public record TemperatureEntry(
        LocalDateTime localDateTime,
        String city,
        double temperature) {
}
