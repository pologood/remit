/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;
import com.sogou.pay.remit.common.Interceptor;
import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;
import com.sogou.pay.remit.manager.UserManager;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年8月4日;
//-------------------------------------------------------
@Component
public class FinalInterceptor extends Interceptor {

  @Autowired
  private UserManager userManager;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    User user = (User) request.getAttribute(UserController.USER_ATTRIBUTE);
    if (Objects.nonNull(user) && ROLES.contains(user.getRole())) return true;
    writeResponse(response, ApiResult.forbidden());
    return false;
  }

  private static final Set<Role> ROLES = Sets.newHashSet(Role.FINAL);

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    userManager.init();
  }
}
