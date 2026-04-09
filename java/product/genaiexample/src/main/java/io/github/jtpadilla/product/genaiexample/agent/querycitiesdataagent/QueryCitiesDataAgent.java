package io.github.jtpadilla.product.genaiexample.agent.querycitiesdataagent;

import com.google.genai.errors.ApiException;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.*;
import io.github.jtpadilla.genai.agent.AgentException;
import io.github.jtpadilla.genai.agent.AgentResources;
import io.github.jtpadilla.genai.generate.GenerateContentException;
import io.github.jtpadilla.genai.generate.GenerateContentWrapper;
import io.github.jtpadilla.genai.schema.SchemaException;
import io.github.jtpadilla.product.genaiexample.schema.CityDataListSchema;

public class QueryCitiesDataAgent {

    static final Part SYSTEM_INSTRUCTION_0 = Part.fromText("""
Todos los prompts que recibirás estarán escritos en español y tu generaras
todo el contenido también en español.
""");

    static final Part SYSTEM_INSTRUCTION_1 = Part.fromText("""
Eres un experto en clima del pais España.
""");

    static final Part SYSTEM_INSTRUCTION_2 = Part.fromText("""
Cuando proporciones fechas asegurate de entregarlas sin zona horaria.
""");

    static private Content getUserPrompt(String city) {
        final String user_query = String.format("""
                Obtén la prevision de las próximas temperaturas aproximadamente en las horas en punto para la ciudad *%s*.
                
                Asegurate que la fecha y hora estén en el formato ISO-8601 *AAAA-MM-DDThh:mm:ss*. 
                """, city
        );
        return Content.fromParts(Part.fromText(user_query));
    }

    static private Tool googleSearchTool() {
        final GoogleSearch googleSearch = GoogleSearch.builder().build();
        return Tool.builder().googleSearch(googleSearch).build();
    }

    static public CityDataListSchema call(AgentResources agentResources, String cityName) throws AgentException {
        try {
            final GenerateContentConfig config = GenerateContentConfig.builder()
                    .tools(googleSearchTool())
                    .systemInstruction(Content.fromParts(SYSTEM_INSTRUCTION_0, SYSTEM_INSTRUCTION_1, SYSTEM_INSTRUCTION_2))
                    .candidateCount(1)
                    .temperature(1.0f)
                    .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0).build())
                    .responseSchema(CityDataListSchema.SCHEMA)
                    .responseMimeType("application/json")
                    .maxOutputTokens(2000)
                    .build();
            final GenerateContentResponse response = agentResources.client().models.generateContent(
                    agentResources.llmModel(),
                    getUserPrompt(cityName),
                    config
            );
            final GenerateContentWrapper contentWrapper = GenerateContentWrapper.create(response);
            if (contentWrapper.text().isEmpty()) {
                throw new AgentException("QueryCitiesDataAgent: La invocacion al LLM no ha proporcionado textos con resultados.");
            }
            return CityDataListSchema.fromJson(contentWrapper.text().get());
        } catch (GenAiIOException | ApiException llmException) {
            throw new AgentException("Error inesperado durante las llamadas al LLM en el agente QueryCitiesDataAgent!", llmException);
        } catch (GenerateContentException contentException) {
            throw new AgentException("El contenido resultante de la llamada al LLM en el agente QueryCitiesDataAgent es incompleto !", contentException);
        } catch (SchemaException schemaException) {
            throw new AgentException("El texto resultante de la llamada al LLM en el agente QueryCitiesDataAgent no es un JSON valido!", schemaException);
        }

    }

}
