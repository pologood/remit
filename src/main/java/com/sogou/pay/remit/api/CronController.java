/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.quartz.CronExpression;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.api.JobController.JobName;
import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月22日;
//-------------------------------------------------------
@Api(name = "cron API", description = "Read/Write/Update/ the cron")
@RestController
@RequestMapping("/api")
public class CronController implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(CronController.class);

  private static Map<JobName, Tuple2<CronTriggerFactoryBean, TriggerKey>> JOB_MAP = new HashMap<>();

  @Autowired
  @Qualifier("payTrigger")
  private CronTriggerFactoryBean payTrigger;

  @Autowired
  @Qualifier("queryTrigger")
  private CronTriggerFactoryBean queryTrigger;

  @Autowired
  @Qualifier("callbackTrigger")
  private CronTriggerFactoryBean callbackTrigger;

  @Autowired
  private SchedulerFactoryBean factory;

  @ApiMethod(description = "update cron")
  @RequestMapping(value = "/cron/{jobName}", method = RequestMethod.PUT)
  public ApiResult<?> pay(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(clazz = JobName.class, name = "jobName", description = "定时任务名") @PathVariable("jobName") JobName jobName,
      @ApiQueryParam(name = "cron", description = "cron expression") @RequestParam(name = "cron") String cron) {
    if (!Objects.equals(Role.ADMIN, user.getRole())) return ApiResult.forbidden();
    Tuple2<CronTriggerFactoryBean, TriggerKey> tuple = JOB_MAP.get(jobName);
    if (Objects.isNull(tuple)) return ApiResult.badRequest("invalid job");
    return reschedule(tuple.f, tuple.s, cron);
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

  @Override
  public void afterPropertiesSet() throws Exception {
    JOB_MAP.put(JobName.pay, new Tuple2<>(payTrigger, new TriggerKey("payTrigger")));
    JOB_MAP.put(JobName.query, new Tuple2<>(queryTrigger, new TriggerKey("queryTrigger")));
    JOB_MAP.put(JobName.callback, new Tuple2<>(callbackTrigger, new TriggerKey("callbackTrigger")));
  }

}
