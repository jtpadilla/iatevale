package io.github.jtpadilla.product.langchain4jexample.service.querytemperature;

import java.util.List;

public interface TemperatureQueryService {
    TemperatureQueryResult query(String provincia, List<String> ciudades) throws TemperatureQueryException;
}
