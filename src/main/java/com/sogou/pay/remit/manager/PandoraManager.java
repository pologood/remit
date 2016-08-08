/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月26日;
//-------------------------------------------------------
@Service
public class PandoraManager implements InitializingBean {

  private static final String BEGIN = "BEGIN", END = "END", RSA = "RSA";

  public static Key PUBLIC_KEY;

  @Autowired
  private Environment env;

  public static Key loadPublicKey(InputStream inputStream) throws Exception {
    String pem = getStringFromPem(IOUtils.readLines(inputStream, StandardCharsets.UTF_8));
    return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pem)));
  }

  private static String getStringFromPem(List<String> list) {
    StringBuilder sb = new StringBuilder();
    for (String s : list) {
      if (s.indexOf(BEGIN) != -1) continue;
      else if (s.indexOf(END) != -1) break;
      sb.append(s);
    }
    return sb.toString();
  }

  public static String decryptPandora(byte[] ptoken) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, PUBLIC_KEY);
    return new String(cipher.doFinal(ptoken));
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    PUBLIC_KEY = loadPublicKey(getClass().getClassLoader().getResourceAsStream(env.getRequiredProperty("pem.path")));
  }

}
