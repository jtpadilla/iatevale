package io.github.jtpadilla.langchain4j.generate;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import io.github.jtpadilla.langchain4j.schema.SchemaEnabled;

import java.util.List;

/**
 * Constructor fluido de mensajes de chat para LangChain4j.
 * Equivalente a {@code PromptBuilder} de la librería google-genai.
 *
 * <p>Acumula bloques de texto (párrafos, títulos, JSON de esquemas y datos)
 * y los materializa en un {@link ChatMessage} (sistema o usuario según el rol).
 *
 * <p>Uso típico:
 * <pre>{@code
 * ChatMessage system = PromptBuilder.builder()
 *     .setRole("system")
 *     .addParagraph("Eres un asistente experto en Java.")
 *     .build();
 *
 * ChatMessage user = PromptBuilder.builder()
 *     .addTitle("Datos de entrada")
 *     .addSchemaData(myData)
 *     .build();
 * }</pre>
 */
public class PromptBuilder {

    private static final String ROLE_SYSTEM = "system";

    public static PromptBuilder builder() {
        return new PromptBuilder();
    }

    private final StringBuilder content;
    private String role;

    public PromptBuilder() {
        this.content = new StringBuilder();
        this.role = null;
    }

    public PromptBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public PromptBuilder addParagraph(String text) {
        content.append(text).append("\n\n");
        return this;
    }

    public PromptBuilder addTitle(String text) {
        content.append(text).append(":\n");
        return this;
    }

    public PromptBuilder addSchemaDefinition(String schemaJson) {
        content.append("```json\n")
               .append(schemaJson)
               .append("\n```\n\n");
        return this;
    }

    public PromptBuilder addSchemaData(SchemaEnabled data) {
        content.append("```json\n")
               .append(data.toJson())
               .append("\n```\n\n");
        return this;
    }

    public PromptBuilder addSchemaDataList(List<SchemaEnabled> dataList) {
        for (SchemaEnabled entry : dataList) {
            content.append("```json\n")
                   .append(entry.toJson())
                   .append("\n```\n\n");
        }
        return this;
    }

    /**
     * Construye el {@link ChatMessage}. Si el rol es {@code "system"} se devuelve un
     * {@link SystemMessage}; en cualquier otro caso, un {@link UserMessage}.
     */
    public ChatMessage build() {
        final String text = content.toString();
        if (ROLE_SYSTEM.equalsIgnoreCase(role)) {
            return SystemMessage.from(text);
        }
        return UserMessage.from(text);
    }

}
