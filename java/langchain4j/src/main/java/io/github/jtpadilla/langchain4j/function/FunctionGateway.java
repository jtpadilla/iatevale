package io.github.jtpadilla.langchain4j.function;

import com.google.gson.Gson;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.github.jtpadilla.langchain4j.function.part.PartToolCallWrapper;
import io.github.jtpadilla.langchain4j.function.part.PartWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bucle agéntico de invocación de herramientas sobre un {@link ChatModel}.
 * Equivalente a {@code FunctionGateway} de la librería google-genai.
 *
 * <p>Orquesta la conversación entre el LLM y las herramientas locales:
 * <ol>
 *   <li>Envía el historial de mensajes al LLM junto con las especificaciones de herramientas.</li>
 *   <li>Si el LLM solicita herramientas, las ejecuta y añade los resultados al historial.</li>
 *   <li>Repite hasta obtener una respuesta de texto o alcanzar el límite de iteraciones.</li>
 * </ol>
 *
 * <p><b>Nota sobre formato JSON:</b> Si se necesita respuesta estructurada en JSON, configura
 * el modelo con {@code GoogleAiGeminiChatModel.builder().responseFormat(ResponseFormat.JSON).build()}
 * antes de pasarlo al gateway. El formato de respuesta es una propiedad del modelo en LangChain4j.
 *
 * <p>Uso típico:
 * <pre>{@code
 * String result = FunctionGateway.builder(model, systemMsg, userMsg, myToolImpl)
 *     .addTool(myToolSpec)
 *     .generate();
 * }</pre>
 */
public class FunctionGateway {

    private static final Gson GSON = new Gson();
    private static final int MAX_ITERATIONS = 5;

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static class Builder {

        private final ChatModel model;
        private final ChatMessage systemPrompt;
        private final ChatMessage userPrompt;
        private final FunctionGatewayCall toolImpl;
        private final List<ToolSpecification> tools;
        private boolean useJsonFormat;

        private Builder(ChatModel model, ChatMessage systemPrompt, ChatMessage userPrompt, FunctionGatewayCall toolImpl) {
            this.model = model;
            this.systemPrompt = systemPrompt;
            this.userPrompt = userPrompt;
            this.toolImpl = toolImpl;
            this.tools = new ArrayList<>();
            this.useJsonFormat = false;
        }

        /**
         * Añade una especificación de herramienta que el LLM puede invocar.
         * Equivalente a {@code addTool(Tool)} del original.
         */
        public Builder addTool(ToolSpecification tool) {
            tools.add(tool);
            return this;
        }

        /**
         * Activa el modo JSON en cada {@link ChatRequest}.
         * Equivalente a {@code setSchema(Schema)} del original: indica al LLM
         * que la respuesta final debe ser JSON válido (la estructura concreta
         * se guía mediante el system prompt).
         */
        public Builder useJsonResponseFormat() {
            this.useJsonFormat = true;
            return this;
        }

        public String generate() throws FunctionGatewayException {
            return new FunctionGateway(model, systemPrompt, userPrompt, toolImpl, tools, useJsonFormat).generate();
        }
    }

    public static Builder builder(ChatModel model, ChatMessage systemPrompt, ChatMessage userPrompt, FunctionGatewayCall toolImpl) {
        return new Builder(model, systemPrompt, userPrompt, toolImpl);
    }

    // -------------------------------------------------------------------------
    // Instancia
    // -------------------------------------------------------------------------

    private final ChatModel model;
    private final FunctionGatewayCall toolImpl;
    private final List<ToolSpecification> tools;
    private final List<ChatMessage> conversationHistory;
    private final boolean useJsonFormat;

    private FunctionGateway(
            ChatModel model,
            ChatMessage systemPrompt,
            ChatMessage userPrompt,
            FunctionGatewayCall toolImpl,
            List<ToolSpecification> tools,
            boolean useJsonFormat) {
        this.model = model;
        this.toolImpl = toolImpl;
        this.tools = List.copyOf(tools);
        this.useJsonFormat = useJsonFormat;
        this.conversationHistory = new ArrayList<>();
        this.conversationHistory.add(systemPrompt);
        this.conversationHistory.add(userPrompt);
    }

    public String generate() throws FunctionGatewayException {

        int limit = MAX_ITERATIONS;

        while (true) {

            if (--limit < 0) {
                throw new FunctionGatewayException("Conversation exceeded maximum number of iterations (" + MAX_ITERATIONS + ")");
            }

            final ChatRequest.Builder requestBuilder = ChatRequest.builder()
                    .messages(conversationHistory)
                    .toolSpecifications(tools);

            if (useJsonFormat) {
                requestBuilder.responseFormat(ResponseFormat.JSON);
            }

            final ChatRequest request = requestBuilder.build();

            final ChatResponse response = model.chat(request);

            final PartWrapper.Summary summary = PartWrapper.extractSummary(response);

            if (!summary.textList().isEmpty() && summary.toolCallList().isEmpty() && summary.otherList().isEmpty()) {
                // Únicamente texto → respuesta final
                return summary.aiMessage().text();

            } else if (summary.textList().isEmpty() && !summary.toolCallList().isEmpty() && summary.otherList().isEmpty()) {
                // Únicamente llamadas a herramientas → ejecutar y continuar
                conversationHistory.add(summary.aiMessage());
                conversationHistory.addAll(executeTools(summary.toolCallList()));

            } else {
                throw new FunctionGatewayException(
                        "Unexpected mix of parts in AiMessage: " +
                        "text=" + summary.textList().size() +
                        ", toolCalls=" + summary.toolCallList().size() +
                        ", other=" + summary.otherList().size());
            }
        }
    }

    private List<ToolExecutionResultMessage> executeTools(List<PartToolCallWrapper> toolCallList) throws FunctionGatewayException {

        final List<ToolExecutionResultMessage> results = new ArrayList<>();

        for (PartToolCallWrapper wrapper : toolCallList) {

            final ToolExecutionRequest request = wrapper.request();

            // Ejecutar la herramienta local
            final Map<String, Object> returnValue = toolImpl.execute(request);

            // Serializar el resultado a JSON para devolverlo al LLM
            final String resultJson = GSON.toJson(returnValue);

            results.add(ToolExecutionResultMessage.from(request, resultJson));
        }

        return results;
    }

}
