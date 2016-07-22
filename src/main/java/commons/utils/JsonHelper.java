package commons.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonHelper {

  public static final ObjectMapper MAPPER = new Jackson2ObjectMapperBuilder()
      .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .serializerByType(Enum.class, new EnumJsonSerializer())
      .serializerByType(LocalDate.class, new LocalDateJsonSerializer())
      .serializerByType(LocalDateTime.class, new LocalDateTimeJsonSerializer()).build()
      .setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_EMPTY);

  public static final TypeReference<Map<String, Object>> TYPE_OF_MAP = new TypeReference<Map<String, Object>>() {};

  public static <T> T fromJson(String src, Class<T> valueType) {
    try {
      return MAPPER.readValue(src, valueType);
    } catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static <T> T fromJson(String src, TypeReference<T> valueTypeRef) {
    try {
      return MAPPER.readValue(src, valueTypeRef);
    } catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static Map<String, Object> toMap(String s) {
    return fromJson(s, TYPE_OF_MAP);
  }

  public static byte[] toJsonBytes(Object value) {
    try {
      return MAPPER.writeValueAsBytes(value);
    } catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static String toJson(Object value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

}
