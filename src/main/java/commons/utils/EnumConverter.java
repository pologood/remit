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
public class EnumConverter<E extends Enum<E>> extends PropertyEditorSupport {

  public static final String METHOD_NAME = "getValue";

  private Class<E> type;

  private final Map<String, E> ENUM_MAP = new HashMap<>();

  public EnumConverter(Class<E> type) {
    try {
      this.type = type;
      Method method = type.getMethod(METHOD_NAME);
      for (E e : type.getEnumConstants())
        ENUM_MAP.put(String.valueOf(method.invoke(e)), e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    E e = ENUM_MAP.get(text);
    if (Objects.isNull(e)) e = Enum.valueOf(type, text);
    setValue(e);
  }

}
