package io.github.jtpadilla.product.langchain4jexample.schema;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import io.github.jtpadilla.langchain4j.schema.SchemaEnabled;
import io.github.jtpadilla.langchain4j.schema.SchemaException;
import io.github.jtpadilla.langchain4j.schema.SchemaGson;

import java.time.LocalDateTime;
import java.util.Objects;

public class CityDataSchema implements SchemaEnabled {

    /** Descripción JSON del esquema, usada en los prompts para guiar la estructura de respuesta. */
    public static final String SCHEMA_JSON = """
            {
              "type": "object",
              "description": "Registro de temperatura de una ciudad en un momento determinado",
              "properties": {
                "city":          { "type": "string", "description": "Nombre de la ciudad" },
                "localdatetime": { "type": "string", "description": "Fecha y hora en formato ISO-8601 sin zona horaria (AAAA-MM-DDThh:mm:ss)" },
                "temperature":   { "type": "number", "description": "Temperatura en grados centígrados" }
              },
              "required": ["city", "localdatetime", "temperature"]
            }
            """;

    private static final Gson GSON = SchemaGson.DEFAULT_GSON;

    public static CityDataSchema fromJson(String jsonString) throws SchemaException {
        try {
            return GSON.fromJson(jsonString, CityDataSchema.class);
        } catch (JsonSyntaxException e) {
            throw new SchemaException("CityDataSchema: json has invalid format", e);
        }
    }

    @SerializedName("city")
    private final String city;

    @SerializedName("localdatetime")
    private final LocalDateTime localDateTime;

    @SerializedName("temperature")
    private final Double temperature;

    public CityDataSchema(String city, LocalDateTime localDateTime, double temperature) {
        this.city = city;
        this.localDateTime = localDateTime;
        this.temperature = temperature;
    }

    public String city() { return city; }
    public LocalDateTime localDateTime() { return localDateTime; }
    public double temperature() { return temperature; }

    @Override
    public String toJson() { return GSON.toJson(this); }

    @Override
    public String toString() {
        return "CityDataSchema{city='" + city + "', localDateTime=" + localDateTime + ", temperature=" + temperature + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CityDataSchema that = (CityDataSchema) o;
        return Objects.equals(city, that.city)
                && Objects.equals(localDateTime, that.localDateTime)
                && Objects.equals(temperature, that.temperature);
    }

    @Override
    public int hashCode() { return Objects.hash(city, localDateTime, temperature); }

}
