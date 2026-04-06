package io.github.jtpadilla.genai.impl;

import com.google.genai.Client;
import io.helidon.config.Config;

public class GenAIServiceGemma3ApiKey {

    final static private String GEMMA_3_4B = "gemma-3-4b-it";
    final static private String GEMMA_3_27B = "gemma-3-27b-it";
    final static private String GEMMA_3_270M = "gemma-3-270m-it";
    final static private String GEMMA_CURRENT = GEMMA_3_27B;

    private final String apiKey;

    public GenAIServiceGemma3ApiKey() {
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
        return GEMMA_CURRENT;
    }

}
