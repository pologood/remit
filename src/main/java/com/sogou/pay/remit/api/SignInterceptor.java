/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.manager.AppManager;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.ErrorCode;

import commons.utils.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月18日;
//-------------------------------------------------------
@Component
public class SignInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Map<String, Object> map = new HashMap<>();
    Enumeration<String> params = request.getParameterNames();
    for (String s; params.hasMoreElements(); map.put(s = params.nextElement(), request.getParameter(s)));

    if (MapUtils.isEmpty(map))
      map = JsonHelper.fromJson(IOUtils.toString(request.getReader()), new TypeReference<Map<String, Object>>() {});

    if (!AppManager.checkSign(map)) {
      writeResponse(response);
      return false;
    } else return true;
  }

  private void writeResponse(HttpServletResponse response) throws Exception {
    PrintWriter writer = null;
    try {
      (writer = response.getWriter())
          .print(JsonHelper.toJson(new ApiResult<>(ErrorCode.BAD_REQUEST, Exceptions.SIGN_INVALID.getErrMsg())));
      writer.flush();
    } finally {
      writer.close();
    }
  }

}
