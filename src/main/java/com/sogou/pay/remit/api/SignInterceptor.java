/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Sets;
import com.sogou.pay.remit.common.Interceptor;
import com.sogou.pay.remit.common.SignHelper;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.manager.AppManager;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月18日;
//-------------------------------------------------------
@Component
public class SignInterceptor extends Interceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (!FILTERED_METHOD.contains(request.getMethod())) return true;
    String theirSign = request.getHeader(PANDORA_SIGN);
    if (StringUtils.isNotBlank(theirSign) && Objects.equals(theirSign, sign(request))) return true;
    writeResponse(response, ApiResult.badRequest(Exceptions.SIGN_INVALID));
    return false;
  }

  private String sign(HttpServletRequest request) throws Exception {
    String sign = null, method = request.getMethod();
    if (RequestMethod.GET.name().equalsIgnoreCase(method)) {
      Map<String, String> map = new HashMap<>();
      request.getParameterMap().entrySet().stream()
          .filter(e -> Objects.nonNull(e.getValue()) && e.getValue().length > 0)
          .forEach(e -> map.put(e.getKey(), e.getValue()[0]));
      sign = AppManager.sign(map);
    } else if (RequestMethod.POST.name().equalsIgnoreCase(method)) sign = sign(IOUtils.toString(request.getReader()));
    return sign;
  }

  public static String sign(String context) {
    return SignHelper.sign(context,
        AppManager.getKey(MapUtils.getInteger(JsonHelper.toMap(context), AppManager.APP_ITEM)));
  }

  public static final String PANDORA_SIGN = "PandoraSign";

  public static final Set<String> FILTERED_METHOD = Sets.newHashSet(RequestMethod.GET.name(),
      RequestMethod.POST.name());

}
