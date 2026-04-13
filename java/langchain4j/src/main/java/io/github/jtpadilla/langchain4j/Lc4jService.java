package io.github.jtpadilla.langchain4j;

import dev.langchain4j.model.chat.ChatModel;

/**
 * Servicio base para obtener una instancia de {@link ChatModel} y el nombre del modelo LLM.
 * Equivalente a {@code GenAIService} de la librería google-genai.
 */
public interface Lc4jService {

    ChatModel createModel();

    String getModelName();

}
