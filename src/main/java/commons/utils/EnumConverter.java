/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月13日;
//-------------------------------------------------------
public class EnumConverter extends PropertyEditorSupport {

  public static final String METHOD_NAME = "getValue";

  private Class<? extends Enum<?>> type;

  private final Map<String, Enum<?>> VALUE_MAP = new HashMap<>();

  private final Map<String, Enum<?>> NAME_MAP = new HashMap<>();

  public EnumConverter(Class<? extends Enum<?>> type) {
    try {
      this.type = type;
      Method method = type.getMethod(METHOD_NAME);
      for (Enum<?> e : type.getEnumConstants()) {
        VALUE_MAP.put(String.valueOf(method.invoke(e)), e);
        NAME_MAP.put(e.name(), e);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    Enum<?> e = VALUE_MAP.get(text);
    if (Objects.isNull(e)) e = NAME_MAP.get(text);
    if (Objects.isNull(e)) throw new IllegalArgumentException(String.format("%s is not a %s", text, type.getName()));
    setValue(e);
  }

}
