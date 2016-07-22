/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.quartz.CronExpression;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月22日;
//-------------------------------------------------------
@Api(name = "cron API", description = "Read/Write/Update/ the cron")
@RestController
@RequestMapping("/api")
public class CronController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CronController.class);

  @Autowired
  @Qualifier("payTrigger")
  private CronTriggerFactoryBean payTrigger;

  @Autowired
  @Qualifier("queryTrigger")
  private CronTriggerFactoryBean queryTrigger;

  @Autowired
  private SchedulerFactoryBean factory;

  private static final TriggerKey PAY_TRIGGER_KEY = new TriggerKey("payTrigger");

  private static final TriggerKey QUERY_TRIGGER_KEY = new TriggerKey("queryTrigger");

  @ApiMethod(description = "update pay cron")
  @RequestMapping(value = "/cron/pay", method = RequestMethod.PUT)
  public ApiResult<?> pay(@RequestParam @NotBlank String cron) {
    return reschedule(payTrigger, PAY_TRIGGER_KEY, cron);
  }

  @ApiMethod(description = "update query cron")
  @RequestMapping(value = "/cron/query", method = RequestMethod.PUT)
  public ApiResult<?> query(@RequestParam @NotBlank String cron) {
    return reschedule(queryTrigger, QUERY_TRIGGER_KEY, cron);
  }

  public ApiResult<?> reschedule(CronTriggerFactoryBean trigger, TriggerKey key, String cron) {
    if (!CronExpression.isValidExpression(cron)) return ApiResult.badRequest("illegal expression");
    try {
      trigger.setCronExpression(cron);
      trigger.afterPropertiesSet();
      factory.getScheduler().rescheduleJob(key, trigger.getObject());
    } catch (Exception e) {
      String errMsg = String.format("reschedule %s error", key.getName());
      LOGGER.error(errMsg, e);
      return ApiResult.internalError(errMsg);
    }
    return ApiResult.ok();
  }

}
