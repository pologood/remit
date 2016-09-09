/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import javax.validation.constraints.Pattern;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiPathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.manager.SmsManager;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年9月7日;
//-------------------------------------------------------
@Api(name = "sms API", description = "send message")
@RestController
@RequestMapping("/api")
public class SmsController {

  @ApiObject(name = "SMS.Status", description = "验证码校验结果", group = "SMS")
  public enum Status {
    success, failed, expired;
  }

  @Autowired
  private SmsManager smsManager;

  @ApiMethod(description = "send code")
  @RequestMapping(value = "/message", method = RequestMethod.POST)
  public ApiResult<?> send(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user) {
    return smsManager.send(user.getMobile());
  }

  @ApiMethod(description = "validate code")
  @RequestMapping(value = "/message/{code}", method = RequestMethod.GET)
  public ApiResult<Status> validate(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "code", description = "验证码") @PathVariable @Pattern(regexp = "^\\d{6}$") String code) {
    return new ApiResult<>(SmsManager.validate(user.getMobile(), code));
  }
}
