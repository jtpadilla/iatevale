package io.github.jtpadilla.langchain4j.schema;

/**
 * Marca un objeto como serializable a JSON para ser incluido en prompts.
 * Idéntico al contrato de {@code SchemaEnabled} de la librería google-genai.
 */
public interface SchemaEnabled {
    String toJson();
}
