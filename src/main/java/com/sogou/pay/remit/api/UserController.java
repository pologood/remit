/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.collections4.MapUtils;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;
import com.sogou.pay.remit.manager.UserManager;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月26日;
//-------------------------------------------------------
@Api(name = "user API", description = "Read/Write/Update/ the user")
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private UserManager userManager;

  @ApiMethod(description = "add user")
  @RequestMapping(value = "/user", method = RequestMethod.POST)
  public ApiResult<?> add(@ApiBodyObject @ModelAttribute @Valid User rookie, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:rookie={}", rookie);
      return ApiResult.bindingResult(bindingResult);
    }
    ApiResult<?> result = userManager.add(rookie);
    return result;
  }

  @ApiMethod(description = "update user")
  @RequestMapping(value = "/user", method = RequestMethod.PUT)
  public ApiResult<?> update(
      @ApiQueryParam(name = "uno", description = "工号", format = "digit") @RequestParam Integer uno,
      @ApiQueryParam(name = "mobile", description = "手机号", required = false) @RequestParam Optional<String> mobile,
      @ApiQueryParam(name = "role", description = "角色") @RequestParam Role role) throws Exception {
    return userManager.update(uno, mobile.orElse(null), role);
  }

  @ApiMethod(description = "get users")
  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public ApiResult<?> get() throws Exception {
    return userManager.list();
  }

  @ApiMethod(description = "get user info")
  @RequestMapping(value = "/user/info", method = RequestMethod.GET)
  public ApiResult<?> getInfo(@ApiQueryParam(name = "token", description = "令牌") @RequestParam String token)
      throws Exception {
    Map<String, Object> map = LogInterceptor.getPtokenDetail(token);
    if (Math.abs(System.currentTimeMillis() - MapUtils.getLongValue(map, "ts")) > LogInterceptor.TIME_INTERVAL)
      return ApiResult.unAuthorized();
    User user = UserManager.getUserByUno(MapUtils.getInteger(map, "uno"));
    return Objects.isNull(user) ? ApiResult.forbidden() : new ApiResult<>(user);
  }

  @ApiMethod(description = "delete user")
  @RequestMapping(value = "/user", method = RequestMethod.DELETE)
  public ApiResult<?> delete(@ApiQueryParam(name = "uno", description = "工号") @RequestParam int uno) throws Exception {
    return userManager.delete(uno);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  public static final String USER_ATTRIBUTE = "remituser";

}
