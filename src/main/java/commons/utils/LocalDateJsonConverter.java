package commons.utils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateJsonConverter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

  static DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Override
  public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return LocalDate.parse(json.getAsString(), dtFmt);

  }

  @Override
  public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.format(dtFmt));
  }
}
