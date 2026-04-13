package io.github.jtpadilla.langchain4j.function.part;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.github.jtpadilla.langchain4j.function.FunctionGatewayException;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracción sellada sobre el contenido de un {@link AiMessage}.
 * Equivalente a {@code PartWrapper} de la librería google-genai.
 *
 * <p>Clasifica el contenido del mensaje en:
 * <ul>
 *   <li>{@link PartTextWrapper} — respuesta de texto</li>
 *   <li>{@link PartToolCallWrapper} — solicitud de invocación de herramienta</li>
 *   <li>{@link PartOtherWrapper} — contenido no reconocido</li>
 * </ul>
 */
public abstract sealed class PartWrapper permits PartTextWrapper, PartToolCallWrapper, PartOtherWrapper {

    public record Summary(
            ChatResponse response,
            AiMessage aiMessage,
            List<PartToolCallWrapper> toolCallList,
            List<PartTextWrapper> textList,
            List<PartOtherWrapper> otherList) {
    }

    public static Summary extractSummary(ChatResponse response) throws FunctionGatewayException {

        final AiMessage aiMessage = response.aiMessage();
        if (aiMessage == null) {
            throw new FunctionGatewayException("ChatResponse does not contain an AiMessage");
        }

        final List<PartWrapper> parts = new ArrayList<>();

        // Texto
        final String text = aiMessage.text();
        if (text != null && !text.isBlank()) {
            parts.add(new PartTextWrapper(text));
        }

        // Llamadas a herramientas
        if (aiMessage.hasToolExecutionRequests()) {
            for (var request : aiMessage.toolExecutionRequests()) {
                parts.add(new PartToolCallWrapper(request));
            }
        }

        // Si no hay ni texto ni herramientas → parte "other"
        if (parts.isEmpty()) {
            parts.add(new PartOtherWrapper());
        }

        return new Summary(
                response,
                aiMessage,
                parts.stream().filter(PartToolCallWrapper.class::isInstance).map(PartToolCallWrapper.class::cast).toList(),
                parts.stream().filter(PartTextWrapper.class::isInstance).map(PartTextWrapper.class::cast).toList(),
                parts.stream().filter(PartOtherWrapper.class::isInstance).map(PartOtherWrapper.class::cast).toList()
        );
    }

}
