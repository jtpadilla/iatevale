package io.github.jtpadilla.langchain4j.function.part;

/**
 * Representa contenido del {@code AiMessage} que no es texto ni llamada a herramienta.
 * Equivalente a {@code PartOtherWrapper} de la librería google-genai.
 */
public final class PartOtherWrapper extends PartWrapper {

    public PartOtherWrapper() {
    }

    @Override
    public String toString() {
        return "PartOtherWrapper{}";
    }

}
