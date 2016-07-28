/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.api.JobController.JobName;
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
  private SchedulerFactoryBean factory;

  @ApiMethod(description = "update cron")
  @RequestMapping(value = "/cron/{jobName}", method = RequestMethod.PUT)
  public ApiResult<?> pay(HttpServletRequest request, @PathVariable("jobName") JobName jobName,
      @RequestParam(name = "cron") @NotBlank String cron) {
    if (!Objects.equals(Role.ADMIN, request.getAttribute("remituser"))) return ApiResult.forbidden();
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
  }

}
