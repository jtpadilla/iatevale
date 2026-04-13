package io.github.jtpadilla.product.langchain4jexample.agent.filteragent;

import dev.langchain4j.data.message.ChatMessage;
import io.github.jtpadilla.langchain4j.agent.AgentException;
import io.github.jtpadilla.langchain4j.agent.AgentResources;
import io.github.jtpadilla.langchain4j.function.FunctionGateway;
import io.github.jtpadilla.langchain4j.function.FunctionGatewayException;
import io.github.jtpadilla.langchain4j.generate.PromptBuilder;
import io.github.jtpadilla.langchain4j.schema.SchemaEnabled;
import io.github.jtpadilla.langchain4j.schema.SchemaException;
import io.github.jtpadilla.product.langchain4jexample.schema.CityDataListSchema;

import java.util.List;

/**
 * Filtra las lecturas de temperatura usando un bucle agéntico con herramientas.
 * Equivalente a {@code FilterAgent} de genaiexample.
 *
 * <p>Diferencias respecto al original:
 * <ul>
 *   <li>{@link PromptBuilder} devuelve {@link ChatMessage} en vez de {@code Content}.</li>
 *   <li>{@link FunctionGateway} usa {@code ToolSpecification}/{@code ToolExecutionRequest}
 *       en vez de {@code Tool}/{@code FunctionCall}.</li>
 *   <li>{@code .useJsonResponseFormat()} reemplaza a {@code .setSchema(Schema)}.</li>
 *   <li>No hay excepciones de la API de Google; los errores del LLM llegan como {@code RuntimeException}.</li>
 * </ul>
 */
public class FilterAgent {

    private static final String SYSTEM_INSTRUCTION_0 = """
            Todos los prompts que recibirás estarán escritos en español y tú generarás
            todo el contenido también en español.
            """;

    private static final String SYSTEM_INSTRUCTION_1 = """
            Eres una herramienta especializada en filtrar datos estructurados.

            Utiliza las herramientas que te han proporcionado para poder realizar estas operaciones.
            """;

    private static final String SYSTEM_INSTRUCTION_2 = """
            Cuando proporciones fechas asegúrate de entregarlas sin zona horaria.
            """;

    private static final String SYSTEM_SCHEMA_INSTRUCTION = """
            Tu respuesta final debe ser ÚNICAMENTE un objeto JSON válido que siga este esquema,
            sin texto adicional y sin bloques de código markdown:
            """;

    private static final String USER_CONTEXT_AND_ROLE = """
            Eres un asistente experto en procesar datos de usuarios.
            """;

    private static final String USER_INSTRUCTIONS = """
            Hay que filtrar los datos que has recibido realizando los siguientes pasos:

            1. Sumariza la lista de ciudades que hay en los datos.
            2. Utiliza la lista de ciudades sumarizada para pasarla a la herramienta, \
               que te indicará en cuáles estamos interesados.
            3. Filtra los datos de entrada quitando los de las ciudades que no nos interesan.
            4. De los datos resultantes deja únicamente para cada pueblo el que tiene la temperatura máxima.
            5. La lista resultante es el resultado final que me interesa.
            """;

    public static CityDataListSchema call(
            AgentResources agentResources,
            List<CityDataListSchema> rawData,
            List<String> ciudades) throws AgentException {

        final FunctionDirectory functionDirectory = new FunctionDirectory(ciudades);

        try {
            final ChatMessage systemPrompt = PromptBuilder.builder()
                    .setRole("system")
                    .addParagraph(SYSTEM_INSTRUCTION_0)
                    .addParagraph(SYSTEM_INSTRUCTION_1)
                    .addParagraph(SYSTEM_INSTRUCTION_2)
                    .addParagraph(SYSTEM_SCHEMA_INSTRUCTION)
                    .addSchemaDefinition(CityDataListSchema.SCHEMA_JSON)
                    .build();

            System.out.println("[SystemPrompt]==============================================");
            System.out.println(systemPrompt);

            final ChatMessage userPrompt = PromptBuilder.builder()
                    .setRole("user")
                    .addParagraph(USER_CONTEXT_AND_ROLE)
                    .addTitle("Los datos de entrada siempre seguirán este esquema JSON")
                    .addSchemaDefinition(CityDataListSchema.SCHEMA_JSON)
                    .addTitle("A continuación, todos los datos que tienes que procesar")
                    .addSchemaDataList(rawData.stream().map(SchemaEnabled.class::cast).toList())
                    .addParagraph(USER_INSTRUCTIONS)
                    .build();

            System.out.println("[UserPrompt]==============================================");
            System.out.println(userPrompt);
            System.out.println("==========================================================");

            final FunctionGateway.Builder gatewayBuilder = FunctionGateway.builder(
                    agentResources.model(),
                    systemPrompt,
                    userPrompt,
                    functionDirectory::executeTool
            ).useJsonResponseFormat();

            for (var spec : FunctionDirectory.specifications()) {
                gatewayBuilder.addTool(spec);
            }

            final String response = gatewayBuilder.generate();
            return CityDataListSchema.fromJson(response);

        } catch (FunctionGatewayException e) {
            throw new AgentException("FilterAgent: error durante la ejecución de funciones.", e);
        } catch (SchemaException e) {
            throw new AgentException("FilterAgent: no se pudo convertir la respuesta del LLM al JSON esperado.", e);
        } catch (RuntimeException e) {
            throw new AgentException("FilterAgent: error inesperado en la llamada al LLM.", e);
        }
    }

}
