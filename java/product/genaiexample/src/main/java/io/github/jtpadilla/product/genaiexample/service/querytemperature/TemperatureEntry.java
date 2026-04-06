package io.github.jtpadilla.product.genaiexample.service.querytemperature;

import java.time.LocalDateTime;

public record TemperatureEntry(
        LocalDateTime localDateTime,
        String city,
        double temperature) {
}
