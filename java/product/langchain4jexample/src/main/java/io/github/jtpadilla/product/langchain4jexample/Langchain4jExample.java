package io.github.jtpadilla.product.langchain4jexample;

import io.github.jtpadilla.langchain4j.Lc4jService;
import io.github.jtpadilla.langchain4j.Lc4jServiceDefault;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.TemperatureQueryException;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.TemperatureQueryResult;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.TemperatureQueryService;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.impl.ServiceImpl;

import java.util.List;

public class Langchain4jExample {

    public static void main(String[] args) {

        // Normalmente resuelto mediante inyección de dependencias
        final Lc4jService lc4jService = new Lc4jServiceDefault();
        final TemperatureQueryService service = new ServiceImpl(lc4jService);

        try {
            final TemperatureQueryResult result = service.query(
                    "Castellón",
                    List.of("Burriana")
            );
            System.out.println(result);
        } catch (TemperatureQueryException e) {
            throw new RuntimeException(e);
        }
    }

}
