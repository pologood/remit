/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sogou.pay.remit.entity.TransferBatch.SignType;
import com.sogou.pay.remit.manager.AppManager;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
public class SignHelper {

  private static final String CHARSET = StandardCharsets.UTF_8.name();

  private static final Logger LOGGER = LoggerFactory.getLogger(SignHelper.class);

  public static String sign(String context, String key, String charset, boolean isAppend, SignType signType) {
    StringBuilder sb = new StringBuilder(context);
    try {
      String s = (isAppend ? sb.append(key) : sb.insert(0, key)).toString();
      LOGGER.debug("signType is {} sign context is {}", signType, s);
      byte[] data = s.getBytes(charset);
      String sign = Objects.equals(signType, SignType.MD5) ? DigestUtils.md5Hex(data) : DigestUtils.sha1Hex(data);
      LOGGER.debug("sign is {}", sign);
      return sign;
    } catch (Exception e) {
      LOGGER.error("[sign]error", e);
      return null;
    }
  }

  public static String sign(String context, String key, SignType signType) {
    return sign(context, key, CHARSET, true, signType);
  }

  public static String sign(Map<String, ?> map, String key) {
    SignType signType = SignType.getSignType(MapUtils.getInteger(map, AppManager.SIGN_TYPE));
    return sign(MapHelper.getSignContext(map), key, Objects.isNull(signType) ? SignType.SHA : signType);
  }

}
