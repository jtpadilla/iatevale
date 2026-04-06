package io.github.jtpadilla.genai;

import com.google.genai.Client;

import java.util.List;

public interface IGenAIService {
    Client createClient();
    String getLlmModel();
}
