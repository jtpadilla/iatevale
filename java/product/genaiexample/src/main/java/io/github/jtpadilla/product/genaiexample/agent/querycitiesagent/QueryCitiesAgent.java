package io.github.jtpadilla.product.genaiexample.agent.querycitiesagent;

import com.google.genai.errors.ApiException;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.*;
import io.github.jtpadilla.genai.agent.AgentException;
import io.github.jtpadilla.genai.agent.AgentResources;
import io.github.jtpadilla.genai.generate.GenerateContentException;
import io.github.jtpadilla.genai.generate.GenerateContentWrapper;
import io.github.jtpadilla.genai.schema.SchemaException;
import io.github.jtpadilla.product.genaiexample.schema.CityListSchema;

public class QueryCitiesAgent {

    static final Part SYSTEM_INSTRUCTION_0 = Part.fromText("""
Todos los prompts que recibirás estarán escritos en español y tu generaras
todo el contenido también en español.
""");

    static final Part SYSTEM_INSTRUCTION_1 = Part.fromText("""
Eres un experto en la geografía del pais España.
""");

    static private Content getUserPrompt(String provincia) {
        final String user_query = String.format("""
                Obtén la lista de los 10 pueblos con mas poblacion de la provincia de `%s` 
                """, provincia
        );
        return Content.fromParts(Part.fromText(user_query));
    }

    static private Tool googleSearchTool() {
        final GoogleSearch googleSearch = GoogleSearch.builder().build();
        return Tool.builder().googleSearch(googleSearch).build();
    }

    static public CityListSchema call(AgentResources agentResources, String provincia) throws AgentException {
        try {
            final GenerateContentConfig config = GenerateContentConfig.builder()
                    .tools(googleSearchTool())
                    .systemInstruction(Content.fromParts(SYSTEM_INSTRUCTION_0, SYSTEM_INSTRUCTION_1))
                    .candidateCount(1)
                    .temperature(1.0f)
                    .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0).build())
                    .responseSchema(CityListSchema.SCHEMA)
                    .responseMimeType("application/json")
                    .maxOutputTokens(1000)
                    .build();
            final GenerateContentResponse response = agentResources.client().models.generateContent(
                    agentResources.llmModel(),
                    getUserPrompt(provincia),
                    config
            );
            final GenerateContentWrapper contentWrapper = GenerateContentWrapper.create(response);
            if (contentWrapper.text().isEmpty()) {
                throw new AgentException("QueryCitiesAgent: La invocacion al LLM no ha proporcionado textos con resultados.");
            }
            return CityListSchema.fromJson(contentWrapper.text().get());
        } catch (GenAiIOException | ApiException llmException) {
            throw new AgentException("Error inesperado durante las llamadas al LLM en el agente QueryCitiesAgent!", llmException);
        } catch (GenerateContentException contentException) {
            throw new AgentException("El contenido resultante de la llamada al LLM en el agente QueryCitiesAgent es incompleto !", contentException);
        } catch (SchemaException schemaException) {
            throw new AgentException("El texto resultante de la llamada al LLM en el agente QueryCitiesAgent no es un JSON valido!", schemaException);
        }
    }

}
