/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.common.Interceptor;
import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.manager.PandoraManager;
import com.sogou.pay.remit.manager.UserManager;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月20日;
//-------------------------------------------------------
@Component
public class LogInterceptor extends Interceptor {

  public static final String PTOKEN = "ptoken";

  public static final long TIME_INTERVAL = TimeUnit.MINUTES.toMillis(30);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String ptoken = request.getParameter(PTOKEN);
    Map<String, Object> map;
    User user = null;
    if (StringUtils.isBlank(ptoken) || MapUtils.isEmpty(map = getPtokenDetail(ptoken))
        || Objects.isNull(user = UserManager.getUserByUno(MapUtils.getInteger(map, "uno")))) {
      writeResponse(response, ApiResult.forbidden());
      return false;
    }
    if (Math.abs(System.currentTimeMillis() - MapUtils.getLongValue(map, "ts")) > TIME_INTERVAL) {
      writeResponse(response, ApiResult.unAuthorized());
      return false;
    }
    request.setAttribute(UserController.USER_ATTRIBUTE, user);
    return true;
  }

  public static Map<String, Object> getPtokenDetail(String ptoken) throws Exception {
    ptoken = PandoraManager.decryptPandora(Base64.getDecoder().decode(ptoken));
    return JsonHelper.toMap(ptoken);
  }

}
