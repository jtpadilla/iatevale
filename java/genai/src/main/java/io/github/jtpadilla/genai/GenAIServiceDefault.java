package io.github.jtpadilla.genai;

import com.google.genai.Client;
import io.helidon.config.Config;

public class GenAIServiceDefault implements GenAIService {


    final static private String GEMINI_3_1_FLASH_LITE_PREVIEW = "gemini-3.1-flash-lite-preview";

    private final String apiKey;

    public GenAIServiceDefault() {
        this.apiKey = Config.global().get("gemma.api-key").asString().orElseThrow(
            () -> new IllegalStateException("Configuration key 'gemma.api-key' is required")
        );
    }

    public Client createClient() {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String getLlmModel() {
        return GEMINI_3_1_FLASH_LITE_PREVIEW;
    }

}
