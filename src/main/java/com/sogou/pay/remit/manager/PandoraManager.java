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
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.Cipher;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月26日;
//-------------------------------------------------------
@Service
public class PandoraManager implements InitializingBean {

  private static String BEGIN = "BEGIN", END = "END", RSA = "RSA", APPID, TOKEN, URL;

  public static Key PUBLIC_KEY;

  @Autowired
  private Environment env;

  @Resource(name = "restTemplate")
  private RestTemplate restTemplate;

  public static Key loadPublicKey(InputStream inputStream) throws Exception {
    String pem = getStringFromPem(IOUtils.readLines(inputStream, StandardCharsets.UTF_8));
    return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pem)));
  }

  private static String getStringFromPem(List<String> list) {
    StringBuilder sb = new StringBuilder();
    for (String s : list)
      if (s.indexOf(BEGIN) != -1) continue;
      else if (s.indexOf(END) != -1) break;
      else sb.append(s);
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
    APPID = env.getRequiredProperty("appId");
    TOKEN = env.getRequiredProperty("token");
    URL = env.getRequiredProperty("push.url");
  }

  public String push(String message) {
    try {
      JsonNode response = restTemplate.postForObject(URL, getParams(message), JsonNode.class);
      return response.get("status").intValue() == 0 ? null : response.get("statusText").textValue();
    } catch (Exception e) {
      return e.toString();
    }
  }

  private Map<String, ?> getParams(String message) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    String now = Long.toString(System.currentTimeMillis());
    map.add("pubid", APPID);
    map.add("token", DigestUtils.md5Hex(String.join(":", APPID, TOKEN, now)));
    map.add("ts", now);
    map.add("to", "all");
    map.add("content", message);
    return map;
  }

}
