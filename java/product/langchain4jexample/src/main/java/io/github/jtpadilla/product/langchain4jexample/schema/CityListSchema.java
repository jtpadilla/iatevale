package io.github.jtpadilla.product.langchain4jexample.schema;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import io.github.jtpadilla.langchain4j.schema.SchemaEnabled;
import io.github.jtpadilla.langchain4j.schema.SchemaException;
import io.github.jtpadilla.langchain4j.schema.SchemaGson;

import java.util.ArrayList;
import java.util.List;

public class CityListSchema implements SchemaEnabled {

    /** Descripción JSON del esquema, usada en los prompts para guiar la estructura de respuesta. */
    public static final String SCHEMA_JSON = """
            {
              "type": "object",
              "description": "Lista de ciudades",
              "properties": {
                "list": {
                  "type": "array",
                  "items": { "type": "string" },
                  "description": "Lista de nombres de ciudades"
                }
              },
              "required": ["list"]
            }
            """;

    private static final Gson GSON = SchemaGson.DEFAULT_GSON;

    public static CityListSchema fromJson(String jsonString) throws SchemaException {
        try {
            final String cleaned = stripMarkdownFences(jsonString);
            return GSON.fromJson(cleaned, CityListSchema.class);
        } catch (JsonSyntaxException e) {
            throw new SchemaException("CityListSchema: json has invalid format", e);
        }
    }

    /** Elimina bloques de código markdown (```json ... ```) que el LLM puede añadir. */
    private static String stripMarkdownFences(String text) {
        if (text == null) return "";
        String trimmed = text.strip();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline >= 0) trimmed = trimmed.substring(firstNewline + 1);
            if (trimmed.endsWith("```")) trimmed = trimmed.substring(0, trimmed.lastIndexOf("```")).stripTrailing();
        }
        return trimmed;
    }

    @SerializedName("list")
    private final List<String> list;

    private CityListSchema(List<String> list) {
        this.list = new ArrayList<>(list);
    }

    public List<String> getList() { return new ArrayList<>(list); }

    @Override
    public String toJson() { return GSON.toJson(this); }

    @Override
    public String toString() { return "CityListSchema{list=" + list + '}'; }

}
