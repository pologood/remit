/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.common;

import java.io.PrintWriter;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年9月22日;
//-------------------------------------------------------
public class Interceptor extends HandlerInterceptorAdapter {

  protected void writeResponse(HttpServletResponse response, ApiResult<?> result) throws Exception {
    PrintWriter writer = null;
    try {
      (writer = response.getWriter()).print(JsonHelper.toJson(result));
      writer.flush();
    } finally {
      if (Objects.nonNull(writer)) writer.close();
    }
  }

}
