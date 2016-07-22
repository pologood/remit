/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.io.IOException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import commons.mybatis.EnumValueTypeHandler;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月19日;
//-------------------------------------------------------
public class EnumJsonSerializer extends JsonSerializer<Enum<?>> {

  @Override
  public void serialize(Enum<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    try {
      Method method = value.getClass().getMethod(EnumValueTypeHandler.METHOD_NAME);
      Object object = method.invoke(value);
      if (object instanceof Integer) gen.writeNumber((Integer) object);
      else gen.writeString(String.valueOf(object));
    } catch (Exception e) {
      gen.writeString(value.name());
    }
  }
}
