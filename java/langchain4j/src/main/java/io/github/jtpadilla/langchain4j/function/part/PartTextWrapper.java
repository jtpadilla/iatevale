package io.github.jtpadilla.langchain4j.function.part;

public final class PartTextWrapper extends PartWrapper {

    private final String text;

    public PartTextWrapper(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return "PartTextWrapper{text='" + text + "'}";
    }

}
