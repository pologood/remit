package commons.utils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;

import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;

public class MapHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(MapHelper.class);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final TypeReference<Map<String, Object>> TYPE_OF_MAP = new TypeReference<Map<String, Object>>() {};

  public static Map<String, Object> make(Object... varArgs) {
    Map<String, Object> map = new HashMap<>();

    for (int i = 0; i < varArgs.length; i += 2) {
      String key = (String) varArgs[i];
      map.put(key, varArgs[i + 1]);
    }

    return map;
  }

  public static MultiValueMap<String, Object> makeMulti(Object... varArgs) {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

    for (int i = 0; i < varArgs.length; i += 2) {
      String key = (String) varArgs[i];
      map.add(key, varArgs[i + 1]);
    }

    return map;
  }

  public static String getSignContext(Map<String, ?> map) {
    if (MapUtils.isEmpty(map)) return StringUtils.EMPTY;
    StringBuilder sb = new StringBuilder();
    map.keySet().stream().sorted().forEach(s -> sb.append(s).append('=').append(map.get(s)).append('&'));
    String result = sb.substring(0, sb.length() - 1);
    LOGGER.debug("sign context is {}", result);
    return result;
  }

  public static Map<String, ?> filter(Map<String, ?> map) {
    return MapUtils.isEmpty(map) ? map
        : map.entrySet().stream().filter(e -> Objects.nonNull(e.getValue()))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  public static Map<String, ?> filter(Map<String, ?> map, Set<String> excludes) {
    return MapUtils.isEmpty(map = filter(map)) || CollectionUtils.isEmpty(excludes)
        || SetUtils.intersection(map.keySet(), excludes).isEmpty() ? map
            : map.entrySet().stream().filter(e -> !excludes.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  public static Map<String, Object> convertToMap(Object o) {
    return MAPPER.convertValue(o, TYPE_OF_MAP);
  }

}
