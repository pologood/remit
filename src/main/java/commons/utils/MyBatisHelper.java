package commons.utils;

import java.util.List;

import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.TypeReference;

import commons.mybatis.EnumValueTypeHandler;

public class MyBatisHelper {

  public static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<List<String>>() {};

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T> void registerEnumHandler(TypeHandlerRegistry register, String packageName) {
    List<Class<? extends Enum<?>>> enums = ReflectUtil.findEnums(packageName);
    for (Class<? extends Enum<?>> clazz : enums) {
      try {
        clazz.getMethod("getValue");
      } catch (Exception e) {
        continue;
      }
      try {
        register.register(clazz, new EnumValueTypeHandler(clazz));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
