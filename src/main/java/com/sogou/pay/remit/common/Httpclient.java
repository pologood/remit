/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.ErrorCode;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月14日;
//-------------------------------------------------------
public class Httpclient {

  private static final String CHARSET_NAME = "GBK";

  public static final Charset CHARSET = Charset.forName(CHARSET_NAME);

  private static final Logger LOGGER = LoggerFactory.getLogger(Httpclient.class);

  private static final int DEFAULT_CONNECTION_TIMEOUT = 8000;

  private static final CloseableHttpClient client = HttpClients.custom()
      .setDefaultRequestConfig(
          RequestConfig.custom().setExpectContinueEnabled(true).setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
              .setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT).build())
      .build();

  public static ApiResult<String> post(String url, String data) {
    return post(url, data, false);
  }

  public static ApiResult<String> post(String url, Map<String, String> data) {
    return post(url, null, CHARSET, false, null, data);
  }

  public static ApiResult<String> post(String url, String data, boolean isJson) {
    return post(url, data, CHARSET, isJson);
  }

  public static ApiResult<String> post(String url, String data, Charset charset, boolean isJson) {
    return post(url, data, charset, isJson, null, null);
  }

  public static ApiResult<String> post(String url, String data, Charset charset, boolean isJson,
      Map<String, String> headers) {
    return post(url, data, charset, isJson, headers, null);
  }

  public static ApiResult<String> post(String url, String data, Charset charset, boolean isJson,
      Map<String, String> headers, Map<String, String> param) {
    if (Objects.isNull(charset)) charset = StandardCharsets.UTF_8;
    long time = System.currentTimeMillis();

    LOGGER.info("post {} url:{} data:{} charset:{}", time, url, data, charset.name());

    HttpPost post = new HttpPost(url);
    HttpEntity entity = Objects.isNull(data) ? new UrlEncodedFormEntity(param.entrySet().stream()
        .map(e -> new BasicNameValuePair(e.getKey(), e.getValue())).collect(Collectors.toList()), charset)
        : new StringEntity(data, charset);
    post.setEntity(entity);
    if (MapUtils.isNotEmpty(headers)) headers.entrySet().forEach(e -> post.setHeader(e.getKey(), e.getValue()));
    if (isJson) post.setHeader("Content-type", "application/json");
    CloseableHttpResponse response = null;

    try {
      response = client.execute(post);

      Integer responseCode = response.getStatusLine().getStatusCode();
      HttpEntity responseEntity = response.getEntity();
      Charset responseCharset = ContentType.getOrDefault(responseEntity).getCharset();
      if (Objects.isNull(responseCharset)) responseCharset = charset;
      String responseData = EntityUtils.toString(responseEntity, charset);

      LOGGER.info("post {} responseCode:{} responseData:{}", time, responseCode, responseData);

      return Objects.equals(HttpStatus.SC_OK, responseCode) ? new ApiResult<>(responseData)
          : new ApiResult<>(ErrorCode.INTERNAL_ERROR, responseData);
    } catch (Exception e) {
      LOGGER.error("post error!", e);
      if (Objects.nonNull(post)) post.abort();
      throw new RuntimeException(e);
    } finally {
      HttpClientUtils.closeQuietly(response);
    }
  }

  public static ApiResult<String> get(String url) {
    long time = System.currentTimeMillis();
    LOGGER.info("get {} url:{} ", time, url);
    HttpGet get = new HttpGet(url);
    CloseableHttpResponse response = null;

    try {
      response = client.execute(get);

      Integer responseCode = response.getStatusLine().getStatusCode();
      HttpEntity responseEntity = response.getEntity();
      Charset responseCharset = ContentType.getOrDefault(responseEntity).getCharset();
      if (Objects.isNull(responseCharset)) responseCharset = StandardCharsets.UTF_8;
      String responseData = EntityUtils.toString(responseEntity, responseCharset);

      LOGGER.info("get {} responseCode:{} responseData:{}", time, responseCode, responseData);

      return Objects.equals(HttpStatus.SC_OK, responseCode) ? new ApiResult<>(responseData)
          : new ApiResult<>(ErrorCode.INTERNAL_ERROR, responseData);
    } catch (Exception e) {
      LOGGER.error("get error!", e);
      if (Objects.nonNull(get)) get.abort();
      throw new RuntimeException(e);
    } finally {
      HttpClientUtils.closeQuietly(response);
    }
  }
}
