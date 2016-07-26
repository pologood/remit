/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.sogou.pay.remit.entity.TransferBatch.SignType;
import com.sogou.pay.remit.manager.AppManager;

import org.bouncycastle.openssl.PEMReader;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
public class SignHelper implements InitializingBean {

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

  public static String decryptPandora(String ptoken) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, key);
    return new String(cipher.doFinal(ptoken.getBytes(StandardCharsets.UTF_8)), CHARSET);

  }

  public Key loadPublicKey(File file) throws Exception {
    PEMReader reader = new PEMReader(new FileReader(file));
    Key pubKey = (Key) reader.readObject();
    reader.close();
    return pubKey;
  }

  private static Key key;

  @Override
  public void afterPropertiesSet() throws Exception {
    key = loadPublicKey(new File("src/main/resources/public_test.pem"));
  }

}
