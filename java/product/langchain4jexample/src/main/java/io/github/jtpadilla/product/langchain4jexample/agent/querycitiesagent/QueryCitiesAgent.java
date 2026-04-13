package io.github.jtpadilla.product.langchain4jexample.agent.querycitiesagent;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.github.jtpadilla.langchain4j.agent.AgentException;
import io.github.jtpadilla.langchain4j.agent.AgentResources;
import io.github.jtpadilla.langchain4j.schema.SchemaException;
import io.github.jtpadilla.product.langchain4jexample.schema.CityListSchema;

/**
 * Consulta al LLM la lista de los 10 municipios más poblados de una provincia española.
 * Equivalente a {@code QueryCitiesAgent} de genaiexample.
 *
 * <p><b>Nota:</b> La versión google-genai usaba Google Search como herramienta de grounding.
 * LangChain4j no expone ese mecanismo de forma genérica, por lo que el agente se apoya
 * en el conocimiento base del modelo (Gemini tiene buena cobertura geográfica de España).
 * El formato de respuesta se guía mediante el system prompt y el schema JSON.
 */
public class QueryCitiesAgent {

    private static final String SYSTEM_TEXT = """
            Todos los prompts que recibirás estarán escritos en español y tú generarás
            todo el contenido también en español.

            Eres un experto en la geografía del país España.

            Responde ÚNICAMENTE con un objeto JSON válido que siga exactamente el siguiente esquema,
            sin texto adicional, sin bloques de código markdown y sin explicaciones:
            """ + CityListSchema.SCHEMA_JSON;

    private static String buildUserText(String provincia) {
        return "Obtén la lista de los 10 pueblos con más población de la provincia de `" + provincia + "`.";
    }

    public static CityListSchema call(AgentResources agentResources, String provincia) throws AgentException {
        try {
            final ChatRequest request = ChatRequest.builder()
                    .messages(
                            SystemMessage.from(SYSTEM_TEXT),
                            UserMessage.from(buildUserText(provincia))
                    )
                    .build();

            final ChatResponse response = agentResources.model().chat(request);
            final String text = response.aiMessage().text();

            if (text == null || text.isBlank()) {
                throw new AgentException("QueryCitiesAgent: el LLM no ha devuelto texto.");
            }

            return CityListSchema.fromJson(text);

        } catch (SchemaException schemaException) {
            throw new AgentException("QueryCitiesAgent: la respuesta del LLM no es el JSON esperado.", schemaException);
        } catch (RuntimeException e) {
            throw new AgentException("QueryCitiesAgent: error inesperado en la llamada al LLM.", e);
        }
    }

}
