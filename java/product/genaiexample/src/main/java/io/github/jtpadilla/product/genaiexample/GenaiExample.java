package io.github.jtpadilla.product.genaiexample;

import io.github.jtpadilla.product.genaiexample.service.querytemperature.TemperatureQueryException;
import io.github.jtpadilla.product.genaiexample.service.querytemperature.TemperatureQueryResult;
import io.github.jtpadilla.product.genaiexample.service.querytemperature.TemperatureQueryService;
import io.github.jtpadilla.product.genaiexample.service.querytemperature.impl.ServiceImpl;
import io.github.jtpadilla.genai.IGenAIService;
import io.github.jtpadilla.genai.impl.GenAIServiceDefault;

import java.util.List;

public class GenaiExample {

    public static void main(String[] args) {

        // Esto normalmente estaría solventado mediante inyección
        final IGenAIService genAIService = new GenAIServiceDefault();
        final TemperatureQueryService service = new ServiceImpl(genAIService);

        // Usando el servicio...
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
