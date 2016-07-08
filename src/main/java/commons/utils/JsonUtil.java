/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public class JsonUtil {

  public static final Gson GSON = new GsonBuilder().setDateFormat(ApiResult.DATE_TIME_FORMAT)
      .registerTypeAdapter(LocalDate.class, new LocalDateJsonConverter())
      .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter()).create();

  public static String toJson(Object o) {
    return GSON.toJson(o);
  }

  public static <T> T fromJson(String json, Class<T> classOfT) {
    return GSON.fromJson(json, classOfT);
  }

  public static <T> T fromJson(String json, Type type) {
    return GSON.fromJson(json, type);
  }

  static class person {

    public String name, age;
  }

  public static void main(String[] args) {
    person p = new person();
    p.name = "wit";
    System.out.println(ReflectionToStringBuilder.toString(p, ToStringStyle.SHORT_PREFIX_STYLE));
  }

}
