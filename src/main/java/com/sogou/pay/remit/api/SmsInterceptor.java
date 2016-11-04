/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.sogou.pay.remit.common.Interceptor;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年9月26日;
//-------------------------------------------------------
@Component
public class SmsInterceptor extends Interceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SmsInterceptor.class);

  public static final long EXPIRY = TimeUnit.MINUTES.toMillis(30);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Cookie[] cookies = request.getCookies();
    LOGGER.info(getCookieString(cookies));
    if (Objects.isNull(cookies)) return error("cookies is null", response, ApiResult.unAuthorized());
    Cookie cookie = Arrays.stream(cookies)
        .filter(c -> Objects.nonNull(c) && Objects.equals(c.getName(), SmsController.COOKIE_NAME) && isValid(c))
        .findFirst().orElse(null);
    if (Objects.isNull(cookie)) return error("no valid cookie", response, ApiResult.unAuthorized());
    return true;
  }

  private boolean isValid(Cookie c) {
    LOGGER.info(String.valueOf(SmsController.cookieMap));
    long now = System.currentTimeMillis(), cookieTime = MapUtils.getLongValue(SmsController.cookieMap, c.getValue());
    LOGGER.info("cookieTime is {} now is{} expiry is {}", cookieTime, now, EXPIRY);
    if (Math.abs(now - cookieTime) < EXPIRY) return true;
    SmsController.cookieMap.remove(c.getValue());
    return false;
  }

  private String getCookieString(Cookie[] cookies) {
    StringBuilder sb = new StringBuilder();
    Arrays.stream(cookies).forEach(c -> sb.append(getCookieString(c)).append(','));
    return sb.length() > 1 ? sb.deleteCharAt(sb.length() - 1).toString() : sb.toString();
  }

  private String getCookieString(Cookie cookie) {
    ToStringHelper helper = MoreObjects.toStringHelper(Cookie.class);
    helper.add("name", cookie.getName());
    helper.add("value", cookie.getValue());
    helper.add("domain", cookie.getDomain());
    helper.add("maxAge", cookie.getMaxAge());
    helper.add("path", cookie.getPath());
    return helper.toString();
  }

  private boolean error(String debugMessage, HttpServletResponse response, ApiResult<?> result) throws Exception {
    if (StringUtils.isNotBlank(debugMessage)) LOGGER.info(debugMessage);
    writeResponse(response, result);
    return false;
  }

}
