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
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;

import commons.mybatis.EnumValueTypeHandler;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月19日;
//-------------------------------------------------------
public class EnumJsonSerializer extends JsonSerializer<Enum<?>> {

  private static final String METHOD_NAME = "getDescription";

  @Override
  public void serialize(Enum<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    try {
      Method method = value.getClass().getMethod(METHOD_NAME);
      gen.writeString((String) method.invoke(value));
      return;
    } catch (Exception e) {}
    try {
      if (Channel.class.isAssignableFrom(value.getClass())) throw new Exception();
      Method method = value.getClass().getMethod(EnumValueTypeHandler.METHOD_NAME);
      Object object = method.invoke(value);
      if (object instanceof Integer) {
        Integer i = (Integer) object;
        if (Status.class.isAssignableFrom(value.getClass())) i = log2(i);
        gen.writeNumber(i);
      } else gen.writeString(String.valueOf(object));
    } catch (Exception e) {
      gen.writeString(value.name());
    }
  }

  private static int log2(int i) {
    if (i < 1 || Integer.bitCount(i) != 1) throw new RuntimeException(String.format("%d is not a 2 power", i));
    int value = 1, count = 0;
    while (value != i) {
      value <<= 1;
      count++;
    }
    return count;
  }
}
