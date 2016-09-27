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

import org.springframework.stereotype.Component;

import com.sogou.pay.remit.common.Interceptor;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年9月26日;
//-------------------------------------------------------
@Component
public class SmsInterceptor extends Interceptor {

  public static final long EXPIRY = TimeUnit.MINUTES.toMillis(30);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Cookie[] cookies = request.getCookies();
    if (Objects.isNull(cookies)) return error(response, ApiResult.forbidden());
    Cookie cookie = Arrays.stream(cookies)
        .filter(c -> Objects.nonNull(c) && Objects.equals(SmsController.DOMAIN, c.getDomain())
            && Objects.equals(c.getName(), SmsController.COOKIE_NAME))
        .sorted((c1, c2) -> (int) (Long.parseLong(c2.getValue()) - Long.parseLong(c1.getValue()))).findFirst()
        .orElse(null);
    if (Objects.isNull(cookie)) return error(response, ApiResult.forbidden());
    if (Math.abs(System.currentTimeMillis() - Long.parseLong(cookie.getValue())) > EXPIRY)
      return error(response, ApiResult.unAuthorized());
    return true;
  }

  private boolean error(HttpServletResponse response, ApiResult<?> result) throws Exception {
    writeResponse(response, result);
    return false;
  }

}
