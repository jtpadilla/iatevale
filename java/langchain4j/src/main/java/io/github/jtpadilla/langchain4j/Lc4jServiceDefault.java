package io.github.jtpadilla.langchain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import io.helidon.config.Config;

/**
 * Implementación por defecto de {@link Lc4jService} que usa Google AI Studio (Gemini)
 * con la API key leída de la configuración de Helidon.
 * Equivalente a {@code GenAIServiceDefault} de la librería google-genai.
 */
public class Lc4jServiceDefault implements Lc4jService {

    private static final String DEFAULT_MODEL_NAME = "gemini-2.0-flash-lite";

    private final String apiKey;

    public Lc4jServiceDefault() {
        this.apiKey = Config.global().get("aistudio-api-key").asString().orElseThrow(
            () -> new IllegalStateException("Configuration key 'aistudio-api-key' is required")
        );
    }

    @Override
    public ChatModel createModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(DEFAULT_MODEL_NAME)
                .build();
    }

    @Override
    public String getModelName() {
        return DEFAULT_MODEL_NAME;
    }

}
