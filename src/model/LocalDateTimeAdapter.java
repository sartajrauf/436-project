package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class LocalDateTimeAdapter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final JsonSerializer<LocalDateTime> serializer = (src, typeOfSrc, context) -> {
        if (src == null) return null;
        String formatted = src.format(formatter);
        JsonPrimitive JsonPrimitive = new JsonPrimitive(formatted);
        return JsonPrimitive;
    };

    public static final JsonDeserializer<LocalDateTime> deserializer = (json, typeOfT, context) -> {
        try {
            return LocalDateTime.parse(json.getAsString(), formatter);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse LocalDateTime", e);
        }
    };
}
