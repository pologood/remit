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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.sogou.pay.remit.enums.Exceptions;
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
  public ApiResult<?> run(@ApiPathParam(name = "jobName", description = "定时任务名") @PathVariable JobName jobName) {
    try {
      Runnable job = JOB_NAME_MAP.get(jobName);
      if (Objects.isNull(job)) return ApiResult.badRequest(Exceptions.JOB_INVALID);
      job.run();
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("run {} job error", jobName, e);
      return ApiResult.internalError(e);
    }
  }

  @ApiMethod(description = "stop job")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.DELETE)
  public ApiResult<?> stop(@ApiPathParam(name = "jobName", description = "定时任务名") @PathVariable JobName jobName) {
    Tuple2<CronTriggerFactoryBean, TriggerKey> tuple = JOB_MAP.get(jobName);
    if (Objects.isNull(tuple)) return ApiResult.badRequest(Exceptions.JOB_INVALID);
    try {
      if (!factory.isRunning()) return ApiResult.ok();
      Scheduler scheduler = factory.getScheduler();
      if (scheduler.checkExists(tuple.s)) scheduler.unscheduleJob(tuple.s);
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("stop {} job error", jobName, e);
      return ApiResult.internalError(e);
    }
  }

  @ApiMethod(description = "start job")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.POST)
  public ApiResult<?> start(@ApiPathParam(name = "jobName", description = "定时任务名") @PathVariable JobName jobName) {
    Tuple2<CronTriggerFactoryBean, TriggerKey> tuple = JOB_MAP.get(jobName);
    if (Objects.isNull(tuple)) return ApiResult.badRequest(Exceptions.JOB_INVALID);
    try {
      if (!factory.isRunning()) factory.start();
      Scheduler scheduler = factory.getScheduler();
      tuple.f.afterPropertiesSet();
      if (!scheduler.checkExists(tuple.s)) scheduler.scheduleJob(tuple.f.getObject());
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("start {} job error", jobName.name(), e);
      return ApiResult.internalError(e);
    }
  }

  @ApiMethod(description = "update cron")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.PUT)
  public ApiResult<?> update(@ApiPathParam(name = "jobName", description = "定时任务名") @PathVariable JobName jobName,
      @ApiQueryParam(name = "cron", description = "定时表达式") @RequestParam(name = "cron") String cron) {
    Tuple2<CronTriggerFactoryBean, TriggerKey> tuple = JOB_MAP.get(jobName);
    if (Objects.isNull(tuple)) return ApiResult.badRequest(Exceptions.JOB_INVALID);
    return reschedule(tuple.f, tuple.s, cron);
  }

  public ApiResult<?> reschedule(CronTriggerFactoryBean trigger, TriggerKey key, String cron) {
    if (!CronExpression.isValidExpression(cron)) return ApiResult.badRequest(Exceptions.CRON_INVALID);
    try {
      trigger.setCronExpression(cron);
      trigger.afterPropertiesSet();
      factory.getScheduler().rescheduleJob(key, trigger.getObject());
    } catch (Exception e) {
      LOGGER.error("reschedule %s error", key.getName(), e);
      return ApiResult.internalError(e);
    }
    return ApiResult.ok();
  }

  @ApiMethod(description = "stop all job")
  @RequestMapping(value = "/job", method = RequestMethod.DELETE)
  public ApiResult<?> stop() {
    try {
      if (factory.isRunning()) factory.stop();
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("stop all job error", e);
      return ApiResult.internalError(e);
    }
  }

  @ApiMethod(description = "start all job")
  @RequestMapping(value = "/job", method = RequestMethod.GET)
  public ApiResult<?> start() {
    try {
      if (!factory.isRunning()) factory.start();
      Scheduler scheduler = factory.getScheduler();
      for (Tuple2<CronTriggerFactoryBean, TriggerKey> tuple : JOB_MAP.values())
        if (!scheduler.checkExists(tuple.s)) {
          tuple.f.afterPropertiesSet();
          scheduler.scheduleJob(tuple.f.getObject());
        }
      return ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("start all job error", e);
      return ApiResult.internalError(e);
    }
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

  private Map<JobName, Runnable> JOB_NAME_MAP = ImmutableMap.of(JobName.pay, () -> transferJob.pay(), JobName.query,
      () -> transferJob.query(), JobName.callback, () -> transferJob.callback());
}
