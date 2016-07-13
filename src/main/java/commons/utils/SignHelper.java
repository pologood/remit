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

import com.sogou.pay.remit.entity.TransferBatch.SignType;
import com.sogou.pay.remit.manager.AppManager;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
public class SignHelper {

  private static final String CHARSET = "UTF-8";

  private static final Logger LOGGER = LoggerFactory.getLogger(SignHelper.class);

  public static String sign(String context, String key, String charset, boolean isAppend, SignType signType) {
    StringBuilder sb = new StringBuilder(context);
    try {
      return DigestUtils.md5Hex((isAppend ? sb.append(key) : sb.insert(0, key)).toString().getBytes(charset));
    } catch (Exception e) {
      LOGGER.error("[sign]error", e);
      return null;
    }
  }

  public static String sign(String context, String key, SignType signType) {
    return sign(context, key, CHARSET, true, signType);
  }

  public static String sign(Map<String, ?> map, String key) {
    SignType signType = SignType.SHA;
    try {
      signType = SignType.valueOf(String.valueOf(map.get(AppManager.SIGN_TYPE)));
    } catch (Exception e) {}
    return sign(MapHelper.getSignContext(map), key, signType);
  }

}
