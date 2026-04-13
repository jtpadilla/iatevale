package io.github.jtpadilla.langchain4j.schema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.jtpadilla.util.gson.JsonInstantAdapter;
import io.github.jtpadilla.util.gson.JsonLocalDateTimeAdapter;
import io.github.jtpadilla.util.gson.JsonZonedDateTimeAdapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Instancia Gson preconfigurada con adaptadores de fecha/hora para implementaciones
 * de {@link SchemaEnabled}. Idéntico a {@code SchemaGson} de la librería google-genai.
 */
public class SchemaGson {

    public static final Gson DEFAULT_GSON = createDefault();

    static Gson createDefault() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new JsonInstantAdapter())
                .registerTypeAdapter(LocalDateTime.class, new JsonLocalDateTimeAdapter())
                .registerTypeAdapter(ZonedDateTime.class, new JsonZonedDateTimeAdapter())
                .create();
    }

}
