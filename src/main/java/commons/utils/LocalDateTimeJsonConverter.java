package commons.utils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeJsonConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

  static DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString(), dtFmt);
  }

  @Override
  public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
    return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(dtFmt));
  }
}
