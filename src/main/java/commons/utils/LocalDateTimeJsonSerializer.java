package commons.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateTimeJsonSerializer extends JsonSerializer<LocalDateTime> {

  public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern(PATTERN);

  @Override
  public void serialize(LocalDateTime dateTime, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    jgen.writeString(dateTime.format(dtFmt));
  }
}
