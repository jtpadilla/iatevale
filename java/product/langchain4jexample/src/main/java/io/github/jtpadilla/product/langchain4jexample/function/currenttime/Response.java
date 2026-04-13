package io.github.jtpadilla.product.langchain4jexample.function.currenttime;

import java.time.LocalDateTime;
import java.util.Map;

class Response {

    private final String currentTime;

    public Response(LocalDateTime currentTime) {
        this.currentTime = currentTime.toString();
    }

    public Map<String, Object> toMap() {
        return Map.of("current_time", currentTime);
    }

}
