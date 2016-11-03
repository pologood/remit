package com.sogou.pay.remit.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import commons.utils.DecimalJsonSerializer;
import commons.utils.LocalDateJsonSerializer;
import commons.utils.LocalDateTimeJsonSerializer;

public class JsonHelper {

  public static final ObjectMapper MAPPER = new Jackson2ObjectMapperBuilder()
      .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .serializerByType(BigDecimal.class, new DecimalJsonSerializer())
      .serializerByType(Enum.class, new EnumJsonSerializer())
      .serializerByType(LocalDate.class, new LocalDateJsonSerializer())
      .serializerByType(LocalDateTime.class, new LocalDateTimeJsonSerializer()).build()
      .setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_EMPTY)
      .registerModule(new Jdk8Module());

  static {
    MAPPER.getFactory().setCharacterEscapes(new XssEscapes());
  }

  public static final TypeReference<Map<String, Object>> TYPE_OF_MAP = new TypeReference<Map<String, Object>>() {};

  public static <T> T fromJson(String src, Class<T> valueType) {
    try {
      return MAPPER.readValue(src, valueType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJson(String src, TypeReference<T> valueTypeRef) {
    try {
      return MAPPER.readValue(src, valueTypeRef);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object> toMap(String s) {
    return fromJson(s, TYPE_OF_MAP);
  }

  /**
   * @return Map String JsonNode List or PoJo convert to String
   * @throws Exception
   */
  public static Map<String, Object> toMap(Object o) throws Exception {
    Map<String, Object> map = new HashMap<>();
    JsonNode root = MAPPER.convertValue(o, JsonNode.class);
    for (Iterator<Entry<String, JsonNode>> iterator = root.fields(); iterator.hasNext();) {
      Entry<String, JsonNode> entry = iterator.next();
      map.put(entry.getKey(), getObject(entry.getValue()));
    }
    return map;
  }

  private static Object getObject(JsonNode node) throws Exception {
    if (Objects.isNull(node)) return null;
    if (node.isBinary()) return node.binaryValue();
    if (node.isBoolean()) return node.booleanValue();
    if (node.isMissingNode()) return node.asText();
    if (node.isNull()) return null;
    if (node.isNumber()) return node.numberValue();
    if (node.isTextual()) return node.asText();
    if (node.isPojo()) return node.asText();
    if (node.isObject()) return node.toString();
    if (node.isArray()) return node.toString();
    throw new RuntimeException(String.format("unknown type:node=%s", node));
  }

  public static byte[] toJsonBytes(Object value) {
    try {
      return MAPPER.writeValueAsBytes(value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String toJson(Object value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static class XssEscapes extends CharacterEscapes {

    private static final int[] ESC = CharacterEscapes.standardAsciiEscapesForJSON();

    private static final Map<Character, Character> MAP = new HashMap<>();

    static {
      MAP.put('(', '（');
      MAP.put(')', '）');
      MAP.put('<', '＜');
      MAP.put('>', '＞');
      MAP.put('\'', '＇');
      MAP.put('"', '＂');
      MAP.keySet().forEach(c -> ESC[c] = ESCAPE_CUSTOM);
    }

    @Override
    public int[] getEscapeCodesForAscii() {
      return ESC;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
      if (ch > 127 || !MAP.containsKey((char) ch)) return null;
      return new SerializedString(Character.toString(MAP.get((char) ch)));
    }
  }
}
