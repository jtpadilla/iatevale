package io.github.jtpadilla.product.genaiexample.service.querytemperature.impl;

import com.google.genai.Client;
import io.github.jtpadilla.product.genaiexample.agent.filteragent.FilterAgent;
import io.github.jtpadilla.product.genaiexample.agent.querycitiesagent.QueryCitiesAgent;
import io.github.jtpadilla.product.genaiexample.agent.querycitiesdataagent.QueryCitiesDataAgent;
import io.github.jtpadilla.product.genaiexample.schema.CityDataListSchema;
import io.github.jtpadilla.product.genaiexample.schema.CityListSchema;
import io.github.jtpadilla.product.genaiexample.service.querytemperature.TemperatureEntry;
import io.github.jtpadilla.product.genaiexample.service.querytemperature.TemperatureQueryResult;
import io.github.jtpadilla.product.genaiexample.service.querytemperature.TemperatureQueryService;
import io.github.jtpadilla.genai.GenAIService;
import io.github.jtpadilla.genai.agent.AgentException;
import io.github.jtpadilla.genai.agent.AgentResources;

import java.util.ArrayList;
import java.util.List;

public class ServiceImpl implements TemperatureQueryService {

    private GenAIService genAIService;

    public ServiceImpl(GenAIService genAIService) {
        this.genAIService = genAIService;
    }

    @Override
    public TemperatureQueryResult query(String provincia, List<String> ciudades) {

        try (Client client = genAIService.createClient()) {

            final AgentResources agentResources = new AgentResources(client, genAIService.getLlmModel());

            // Obtenemos la lista de ciudades con mas poblacion de la provincia proporcionada como parámetro
            CityListSchema cityList = QueryCitiesAgent.call(agentResources, provincia);
            System.out.println("[Lista de ciudades]");
            System.out.println(cityList.toJson());

            // Para cada una de las ciudades obtenemos la prevision de temperaturas
            final List<CityDataListSchema> dataList = new ArrayList<>();
            for (String cityName : cityList.getList()) {
                CityDataListSchema cityDataList = QueryCitiesDataAgent.call(agentResources, cityName);
                dataList.add(cityDataList);
                System.out.format("[Temperaturas para %s%n]", cityName);
                System.out.println(cityDataList.toJson());
            }

            // Finalmente, filtramos las lecturas que sean de determinadas ciudades y
            // únicamente la más alta para cada ciudad.
            final CityDataListSchema result = FilterAgent.call(agentResources, dataList, ciudades);

            // Se convierte el resultado
            return new TemperatureQueryResult(
                result.getList().stream()
                        .map(entry->new TemperatureEntry(entry.localDateTime(), entry.city(), entry.temperature()))
                        .toList()
            );

        } catch (AgentException e) {
            throw new RuntimeException(e);
        }
    }

}
