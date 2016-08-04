/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.manager.AppManager;
import com.sogou.pay.remit.manager.UserManager;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年8月2日;
//-------------------------------------------------------
@Api(name = "refresh API", description = "refresh settings")
@RestController
@RequestMapping("/api")
public class RefreshController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshController.class);

  @Autowired
  private UserManager userManager;

  @Autowired
  private AppManager appManager;

  @ApiMethod(description = "refresh settings")
  @RequestMapping(value = "/refresh", method = RequestMethod.GET)
  public ApiResult<?> refresh() {
    try {
      userManager.afterPropertiesSet();
      appManager.init();
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("refresh error", e);
      return ApiResult.internalError(e);
    }
  }
}
