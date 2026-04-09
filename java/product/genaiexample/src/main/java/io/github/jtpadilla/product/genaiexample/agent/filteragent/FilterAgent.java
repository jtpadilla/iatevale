package io.github.jtpadilla.product.genaiexample.agent.filteragent;

import com.google.genai.errors.ApiException;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.Content;
import io.github.jtpadilla.genai.agent.AgentException;
import io.github.jtpadilla.genai.agent.AgentResources;
import io.github.jtpadilla.genai.function.FunctionGateway;
import io.github.jtpadilla.genai.function.FunctionGatewayException;
import io.github.jtpadilla.genai.generate.PromptBuilder;
import io.github.jtpadilla.genai.schema.SchemaEnabled;
import io.github.jtpadilla.genai.schema.SchemaException;
import io.github.jtpadilla.product.genaiexample.schema.CityDataListSchema;

import java.util.List;

public class FilterAgent {

    static final String SYSTEM_INSTRUCTION_0 = """
        Todos los prompts que recibirás estarán escritos en español y tu generaras
        todo el contenido también en español.
        """;

    static final String SYSTEM_INSTRUCTION_1 = """
        Eres una herramienta que especializada en filtrar datos estructurados.
        
        Utiliza las herramientas que te han proporcionado para poder realizar estas operaciones.
        """;

    static final String SYSTEM_INSTRUCTION_2 = """
        Cuando proporciones fechas asegurate de entregarlas sin zona horaria.
        """;

    static final String USER_CONTEXT_AND_ROLE = """
        Eres un asistente experto en procesar datos de usuarios.
        """;

    static final String USER_INSTRUCTIONS = """
        Hay que filtrar los datos que has recibido realizando los siguientes pasos:
        
        1. Sumariza la lista de ciudades que hay en los datos
        2. Utiliza la lista de ciudades que has sumarizado para pasarla a la herramienta y que esta te indique en cuales estamos interesados.
        3. Filtra los datos de entrada quitando los de las ciudades que no nos interesan.
        4. De los datos resultantes deja únicamente para cada pueblo el que tiene la maxima temperatura.
        5. La lista resultante es el resultado final que me interesa.  
        """;

    static public CityDataListSchema call(AgentResources agentResources, List<CityDataListSchema> rawData, List<String> ciudades) throws AgentException {
        final FunctionDirectory functionDirectory = new FunctionDirectory(ciudades);
        try {

            final Content systemPrompt = PromptBuilder.builder()
                    .setRule("system")
                    .addParagraph(SYSTEM_INSTRUCTION_0)
                    .addParagraph(SYSTEM_INSTRUCTION_1)
                    .addParagraph(SYSTEM_INSTRUCTION_2)
                    .build();
            System.out.println("[SystemPrompt]==============================================");
            System.out.println(systemPrompt.toJson());

            final Content userPrompt = PromptBuilder.builder()
                    .setRule("user")
                    .addParagraph(USER_CONTEXT_AND_ROLE)
                    .addTitle("Los datos de entrada siempre seguirán este esquema JSON")
                    .addSchemaDefinition(CityDataListSchema.SCHEMA)
                    .addTitle("A continuación, todos los datos que tienes que procesar")
                    .addSchemaDataList(rawData.stream().map(SchemaEnabled.class::cast).toList())
                    .addParagraph(USER_INSTRUCTIONS)
                    .build();
            System.out.println("[UserPrompt]==============================================");
            System.out.println(userPrompt.toJson());
            System.out.println("==========================================================");

            final String response = FunctionGateway.builder(
                            agentResources.llmModel(),
                            agentResources.client(),
                            systemPrompt,
                            userPrompt,
                            functionDirectory::executeTool)
                    .addTool(FunctionDirectory.createTool())
                    .setSchema(CityDataListSchema.SCHEMA)
                    .generate();
            return CityDataListSchema.fromJson(response);
        } catch (GenAiIOException | ApiException llmException) {
            throw new AgentException("FilterAgent: Error inesperado durante las llamadas al LLM!", llmException);
        } catch (FunctionGatewayException functionGatewayException) {
            throw new AgentException("FilterAgentError durante la ejecucion de funciones en el agente!", functionGatewayException);
        } catch (SchemaException schemaException) {
            throw new AgentException("FilterAgent: No se ha podido convertir la respuesta del LLM al json esperado!", schemaException);
        }
    }

}
