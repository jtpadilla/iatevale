package io.github.jtpadilla.genai;

import com.google.genai.Client;

public interface GenAIService {
    Client createClient();
    String getLlmModel();
}
