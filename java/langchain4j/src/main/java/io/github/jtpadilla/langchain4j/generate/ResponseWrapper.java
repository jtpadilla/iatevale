package io.github.jtpadilla.langchain4j.generate;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;

import java.util.Optional;

/**
 * Envuelve una {@link ChatResponse} de una llamada de generación simple (sin herramientas).
 * Equivalente a {@code GenerateContentWrapper} de la librería google-genai.
 *
 * <p>Garantiza que la respuesta contiene texto y expone el uso de tokens.
 */
public class ResponseWrapper {

    public static ResponseWrapper create(ChatResponse response) throws GenerateException {

        final String text = response.aiMessage().text();

        if (text == null || text.isBlank()) {
            throw new GenerateException("ChatResponse does not contain text content");
        }

        final TokenUsage tokenUsage = response.metadata() != null
                ? response.metadata().tokenUsage()
                : null;

        return new ResponseWrapper(text, tokenUsage);
    }

    private final String text;
    private final TokenUsage tokenUsage;

    private ResponseWrapper(String text, TokenUsage tokenUsage) {
        this.text = text;
        this.tokenUsage = tokenUsage;
    }

    public Optional<String> text() {
        return Optional.of(text);
    }

    public Optional<TokenUsage> usage() {
        return Optional.ofNullable(tokenUsage);
    }

    @Override
    public String toString() {
        return "ResponseWrapper{" +
                "text='" + text + '\'' +
                ", tokenUsage=" + tokenUsage +
                '}';
    }

}
