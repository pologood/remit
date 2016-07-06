/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public class JsonUtil {

  private static final Gson GSON = new GsonBuilder().create();

  public static String toJson(Object o) {
    return GSON.toJson(o);
  }

  public static <T> T fromjson(String json, Class<T> classOfT) {
    return GSON.fromJson(json, classOfT);
  }

}
