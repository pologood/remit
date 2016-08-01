/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.MapUtils;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ApiResult<?> add(@RequestAttribute(name = USER_ATTRIBUTE) User admin,
      @ApiBodyObject(clazz = User.class) @RequestBody @Valid User rookie, BindingResult bindingResult)
          throws Exception {
    if (Objects.equals(admin.getRole(), Role.FINAL)) return ApiResult.unAuthorized();
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:rookie={}", rookie);
      return ApiResult.bindingResult(bindingResult);
    }
    return userManager.add(rookie);
  }

  @ApiMethod(description = "update user")
  @RequestMapping(value = "/user", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestAttribute(name = USER_ATTRIBUTE) User admin,
      @ApiQueryParam(name = "uno", description = "工号", format = "\\d+") @RequestParam(name = "uno") @NotNull Integer uno,
      @ApiQueryParam(name = "mobile", description = "手机号", required = false) @RequestParam(name = "mobile", required = false) Optional<String> mobile,
      @ApiQueryParam(name = "role", description = "角色", required = false) @RequestParam(name = "role", required = false) Optional<Role> role)
          throws Exception {
    if (Objects.equals(admin.getRole(), Role.FINAL)) return ApiResult.unAuthorized();
    return userManager.update(uno, mobile.orElse(null), role.orElse(null));
  }

  @ApiMethod(description = "get users")
  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public ApiResult<?> get(@RequestAttribute(name = USER_ATTRIBUTE) User admin) throws Exception {
    return userManager.list();
  }

  @ApiMethod(description = "get users")
  @RequestMapping(value = "/user/token", method = RequestMethod.GET)
  public ApiResult<?> getInfo(
      @ApiQueryParam(name = "ptoken", description = "令牌") @RequestParam(name = "ptoken") String token)
          throws Exception {
    User user = UserManager.getUserByUno(MapUtils.getInteger(LogInterceptor.getPtokenDetail(token), "uno"));
    return Objects.isNull(user) ? ApiResult.forbidden() : new ApiResult<>(user);
  }

  @ApiMethod(description = "delete user")
  @RequestMapping(value = "/user", method = RequestMethod.DELETE)
  public ApiResult<?> delete(@RequestAttribute(name = USER_ATTRIBUTE) User admin,
      @ApiQueryParam(name = "uno", description = "工号") @RequestParam(name = "uno") @NotNull Integer uno)
          throws Exception {
    if (Objects.equals(admin.getRole(), Role.FINAL)) return ApiResult.unAuthorized();
    return userManager.delete(uno);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  public static final String USER_ATTRIBUTE = "remituser";

}
