/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
  public ApiResult<?> add(HttpServletRequest request, @ApiQueryParam @RequestBody @Valid User user,
      BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:user={}", user);
      return ApiResult.bindingResult(bindingResult);
    }
    return userManager.add(user);
  }

  @ApiMethod(description = "update user")
  @RequestMapping(value = "/user", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestParam(name = "uno") @NotNull Integer uno,
      @RequestParam(name = "mobile", required = false) Optional<String> mobile,
      @RequestParam(name = "role", required = false) Optional<Role> role) throws Exception {
    return userManager.update(uno, mobile.orElse(null), role.orElse(null));
  }

  @ApiMethod(description = "get users")
  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public ApiResult<?> get() throws Exception {
    return userManager.list();
  }

  @ApiMethod(description = "delete user")
  @RequestMapping(value = "/user", method = RequestMethod.DELETE)
  public ApiResult<?> delete(@RequestParam(name = "uno") @NotNull Integer uno) throws Exception {
    return userManager.delete(uno);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

}
