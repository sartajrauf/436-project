package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

public class LocalDateTimeAdapter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final JsonSerializer<LocalDateTime> serializer = (src, typeOfSrc, context) -> {
        return src == null ? null : new JsonObject().getAsJsonPrimitive(src.format(formatter));
    };

    public static final JsonDeserializer<LocalDateTime> deserializer = (json, typeOfT, context) -> {
        try {
            return LocalDateTime.parse(json.getAsString(), formatter);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse LocalDateTime", e);
        }
    };
}
