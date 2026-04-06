package io.github.jtpadilla.product.genaiexample.service.querytemperature;

import java.util.List;

public interface TemperatureQueryService {
    TemperatureQueryResult query(String provincia, List<String> ciudades) throws TemperatureQueryException;
}
