/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
public class SignHelper {

  private static final String CHARSET = "UTF-8";

  private static final Logger LOGGER = LoggerFactory.getLogger(SignHelper.class);

  public static String sign(String context, String key, String charset, boolean isAppend) {
    StringBuilder sb = new StringBuilder(context);
    try {
      return DigestUtils.md5Hex((isAppend ? sb.append(key) : sb.insert(0, key)).toString().getBytes(charset));
    } catch (Exception e) {
      LOGGER.error("[sign]error", e);
      return null;
    }
  }

  public static String sign(String context, String key) {
    return sign(context, key, CHARSET, true);
  }

  public static String sign(Map<String, ?> map, String key) {
    return sign(MapHelper.getSignContext(map), key);
  }

}
