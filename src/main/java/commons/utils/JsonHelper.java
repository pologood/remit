package commons.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

  private static final ObjectMapper MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);

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
