package io.github.jtpadilla.product.langchain4jexample.service.querytemperature.impl;

import io.github.jtpadilla.langchain4j.Lc4jService;
import io.github.jtpadilla.langchain4j.agent.AgentException;
import io.github.jtpadilla.langchain4j.agent.AgentResources;
import io.github.jtpadilla.product.langchain4jexample.agent.filteragent.FilterAgent;
import io.github.jtpadilla.product.langchain4jexample.agent.querycitiesagent.QueryCitiesAgent;
import io.github.jtpadilla.product.langchain4jexample.agent.querycitiesdataagent.QueryCitiesDataAgent;
import io.github.jtpadilla.product.langchain4jexample.schema.CityDataListSchema;
import io.github.jtpadilla.product.langchain4jexample.schema.CityListSchema;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.TemperatureEntry;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.TemperatureQueryResult;
import io.github.jtpadilla.product.langchain4jexample.service.querytemperature.TemperatureQueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de consulta de temperaturas usando LangChain4j.
 * Equivalente a {@code ServiceImpl} de genaiexample.
 *
 * <p>Diferencias clave respecto al original:
 * <ul>
 *   <li>{@link Lc4jService} reemplaza a {@code GenAIService}.</li>
 *   <li>{@link AgentResources#of(Lc4jService)} crea los recursos; no hay {@code Client} AutoCloseable,
 *       por lo que no se necesita {@code try-with-resources}.</li>
 *   <li>El {@code ChatModel} se crea una sola vez y se reutiliza entre agentes.</li>
 * </ul>
 */
public class ServiceImpl implements TemperatureQueryService {

    private final Lc4jService lc4jService;

    public ServiceImpl(Lc4jService lc4jService) {
        this.lc4jService = lc4jService;
    }

    @Override
    public TemperatureQueryResult query(String provincia, List<String> ciudades) {

        // ChatModel es stateless: se crea una vez y se comparte entre todos los agentes.
        final AgentResources agentResources = AgentResources.of(lc4jService);

        try {
            // Obtenemos la lista de ciudades con más población de la provincia dada
            CityListSchema cityList = QueryCitiesAgent.call(agentResources, provincia);
            System.out.println("[Lista de ciudades]");
            System.out.println(cityList.toJson());

            // Para cada ciudad obtenemos la previsión de temperaturas
            final List<CityDataListSchema> dataList = new ArrayList<>();
            for (String cityName : cityList.getList()) {
                CityDataListSchema cityDataList = QueryCitiesDataAgent.call(agentResources, cityName);
                dataList.add(cityDataList);
                System.out.printf("[Temperaturas para %s]%n", cityName);
                System.out.println(cityDataList.toJson());
            }

            // Filtramos: solo las ciudades de interés, únicamente la temperatura máxima por ciudad
            final CityDataListSchema result = FilterAgent.call(agentResources, dataList, ciudades);

            return new TemperatureQueryResult(
                    result.getList().stream()
                            .map(entry -> new TemperatureEntry(entry.localDateTime(), entry.city(), entry.temperature()))
                            .toList()
            );

        } catch (AgentException e) {
            throw new RuntimeException(e);
        }
    }

}
