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
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.quartz.CronExpression;
import org.quartz.Scheduler;
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

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;
import com.sogou.pay.remit.job.TransferJob;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Api(name = "job API", description = "Read/Write/Update/ the job ")
@RestController
@RequestMapping("/api")
public class JobController implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);

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

  @Autowired
  private TransferJob transferJob;

  private static Map<JobName, Tuple2<CronTriggerFactoryBean, TriggerKey>> JOB_MAP = new HashMap<>();

  @ApiMethod(description = "run job")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.GET)
  public ApiResult<?> run(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "jobName", clazz = JobName.class, description = "定时任务名") @PathVariable JobName jobName) {
    if (!Objects.equals(Role.ADMIN, user.getRole())) return ApiResult.forbidden();
    try {
      if (Objects.equals(JobName.pay, jobName)) transferJob.pay();
      else if (Objects.equals(JobName.query, jobName)) transferJob.query();
      else if (Objects.equals(JobName.callback, jobName)) transferJob.callback();
      else return ApiResult.notImplemented();
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("run {} job error", jobName.name(), e);
      return ApiResult.internalError(e.getMessage());
    }
  }

  @ApiMethod(description = "stop job")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.DELETE)
  public ApiResult<?> stop(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(clazz = JobName.class, name = "jobName", description = "定时任务名") @PathVariable("jobName") JobName jobName) {
    if (!Objects.equals(Role.ADMIN, user.getRole())) return ApiResult.forbidden();
    Tuple2<CronTriggerFactoryBean, TriggerKey> tuple = JOB_MAP.get(jobName);
    if (Objects.isNull(tuple)) return ApiResult.badRequest("invalid job");
    try {
      if (!factory.isRunning()) return ApiResult.ok();
      Scheduler scheduler = factory.getScheduler();
      if (scheduler.checkExists(tuple.s)) scheduler.unscheduleJob(tuple.s);
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("stop {} job error", jobName.name(), e);
      return ApiResult.internalError(e.getMessage());
    }
  }

  @ApiMethod(description = "start job")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.POST)
  public ApiResult<?> start(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(clazz = JobName.class, name = "jobName", description = "定时任务名") @PathVariable("jobName") JobName jobName) {
    if (!Objects.equals(Role.ADMIN, user.getRole())) return ApiResult.forbidden();
    Tuple2<CronTriggerFactoryBean, TriggerKey> tuple = JOB_MAP.get(jobName);
    if (Objects.isNull(tuple)) return ApiResult.badRequest("invalid job");
    try {
      if (!factory.isRunning()) factory.start();
      Scheduler scheduler = factory.getScheduler();
      if (!scheduler.checkExists(tuple.s)) scheduler.scheduleJob(tuple.f.getObject());
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("start {} job error", jobName.name(), e);
      return ApiResult.internalError(e.getMessage());
    }
  }

  @ApiMethod(description = "update cron")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.PUT)
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
      LOGGER.error("reschedule %s error", key.getName(), e);
      return ApiResult.internalError(e.getMessage());
    }
    return ApiResult.ok();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    JOB_MAP.put(JobName.pay, new Tuple2<>(payTrigger, new TriggerKey("payTrigger")));
    JOB_MAP.put(JobName.query, new Tuple2<>(queryTrigger, new TriggerKey("queryTrigger")));
    JOB_MAP.put(JobName.callback, new Tuple2<>(callbackTrigger, new TriggerKey("callbackTrigger")));
  }

  @ApiObject(name = "JobName", description = "定时任务名")
  public enum JobName {
    pay, query, callback;
  }
}
