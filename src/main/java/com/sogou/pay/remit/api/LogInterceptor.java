/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.manager.PandoraManager;
import com.sogou.pay.remit.manager.UserManager;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月20日;
//-------------------------------------------------------
public class LogInterceptor extends HandlerInterceptorAdapter {

  private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

  private static final long TIME_INTERVAL = TimeUnit.MINUTES.toMillis(30);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String ptoken = request.getParameter("ptoken");
    Map<String, Object> map;
    Long timestamp = null;
    Integer uno = null;
    User user = null;
    if (StringUtils.isBlank(ptoken) || MapUtils.isEmpty(map = getPtokenDetail(ptoken))
        || Objects.isNull(timestamp = MapUtils.getLong(map, "ts"))
        || Math.abs(System.currentTimeMillis() - timestamp.longValue()) > TIME_INTERVAL
        || Objects.isNull(uno = MapUtils.getInteger(map, "uno"))) {
      response.sendRedirect(getUrl(request));
      return false;
    }
    if (Objects.isNull(user = UserManager.getUserByUno(uno))) {
      SignInterceptor.writeResponse(response, ApiResult.forbidden());
      return false;
    }
    request.setAttribute("remituser", user);
    return true;
  }

  private Map<String, Object> getPtokenDetail(String ptoken) throws Exception {
    ptoken = URLDecoder.decode(ptoken, DEFAULT_CHARSET);
    ptoken = PandoraManager.decryptPandora(Base64.getDecoder().decode(ptoken.replace(' ', '+')));
    return JsonHelper.toMap(ptoken);
  }

  private String getUrl(HttpServletRequest request) throws UnsupportedEncodingException {
    StringBuffer sb = request.getRequestURL().append('?');
    Enumeration<String> names = request.getParameterNames();
    for (String s; names.hasMoreElements();)
      sb.append(s = names.nextElement()).append('=').append(request.getParameter(s)).append('&');
    return StringUtils.join(PandoraManager.PANDORA_URL,
        URLEncoder.encode(sb.substring(0, sb.length() - 1), DEFAULT_CHARSET));
  }

}
