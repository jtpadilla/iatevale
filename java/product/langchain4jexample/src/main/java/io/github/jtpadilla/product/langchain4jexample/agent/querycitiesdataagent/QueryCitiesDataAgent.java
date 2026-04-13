package io.github.jtpadilla.product.langchain4jexample.agent.querycitiesdataagent;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.github.jtpadilla.langchain4j.agent.AgentException;
import io.github.jtpadilla.langchain4j.agent.AgentResources;
import io.github.jtpadilla.langchain4j.schema.SchemaException;
import io.github.jtpadilla.product.langchain4jexample.schema.CityDataListSchema;

/**
 * Consulta al LLM la previsión horaria de temperaturas para una ciudad española.
 * Equivalente a {@code QueryCitiesDataAgent} de genaiexample.
 *
 * <p><b>Nota:</b> La versión google-genai usaba Google Search para obtener datos en tiempo real.
 * LangChain4j no expone ese mecanismo de forma genérica; el agente se apoya en el conocimiento
 * base del modelo (aproximaciones de temperatura típica, no datos meteorológicos en tiempo real).
 */
public class QueryCitiesDataAgent {

    private static final String SYSTEM_TEXT = """
            Todos los prompts que recibirás estarán escritos en español y tú generarás
            todo el contenido también en español.

            Eres un experto en clima del país España.

            Cuando proporciones fechas asegúrate de entregarlas sin zona horaria,
            en formato ISO-8601: AAAA-MM-DDThh:mm:ss

            Responde ÚNICAMENTE con un objeto JSON válido que siga exactamente el siguiente esquema,
            sin texto adicional, sin bloques de código markdown y sin explicaciones:
            """ + CityDataListSchema.SCHEMA_JSON;

    private static String buildUserText(String city) {
        return String.format("""
                Obtén la previsión de las próximas temperaturas aproximadamente en las horas \
                en punto para la ciudad *%s*.

                Asegúrate que la fecha y hora estén en el formato ISO-8601 *AAAA-MM-DDThh:mm:ss*.
                """, city);
    }

    public static CityDataListSchema call(AgentResources agentResources, String cityName) throws AgentException {
        try {
            final ChatRequest request = ChatRequest.builder()
                    .messages(
                            SystemMessage.from(SYSTEM_TEXT),
                            UserMessage.from(buildUserText(cityName))
                    )
                    .build();

            final ChatResponse response = agentResources.model().chat(request);
            final String text = response.aiMessage().text();

            if (text == null || text.isBlank()) {
                throw new AgentException("QueryCitiesDataAgent: el LLM no ha devuelto texto.");
            }

            return CityDataListSchema.fromJson(text);

        } catch (SchemaException schemaException) {
            throw new AgentException("QueryCitiesDataAgent: la respuesta del LLM no es el JSON esperado.", schemaException);
        } catch (RuntimeException e) {
            throw new AgentException("QueryCitiesDataAgent: error inesperado en la llamada al LLM.", e);
        }
    }

}
