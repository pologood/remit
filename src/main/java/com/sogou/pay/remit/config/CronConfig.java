/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月22日;
//-------------------------------------------------------
@Configuration
public class CronConfig {

  @Autowired
  Environment env;

  @Bean
  public MethodInvokingJobDetailFactoryBean payJob() {
    MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
    bean.setTargetBeanName("transferJob");
    bean.setTargetMethod("pay");
    bean.setConcurrent(false);
    return bean;
  }

  @Bean(name = "payTrigger")
  public CronTriggerFactoryBean payTrigger() {
    CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
    bean.setJobDetail(payJob().getObject());
    bean.setCronExpression(env.getProperty("remit.pay.cron"));
    bean.setName("payTrigger");
    return bean;
  }

  @Bean
  public MethodInvokingJobDetailFactoryBean queryJob() {
    MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
    bean.setTargetBeanName("transferJob");
    bean.setTargetMethod("query");
    bean.setConcurrent(false);
    return bean;
  }

  @Bean(name = "queryTrigger")
  public CronTriggerFactoryBean queryTrigger() {
    CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
    bean.setJobDetail(queryJob().getObject());
    bean.setCronExpression(env.getProperty("remit.query.cron"));
    bean.setName("queryTrigger");
    return bean;
  }

  @Bean
  public MethodInvokingJobDetailFactoryBean callbackJob() {
    MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
    bean.setTargetBeanName("transferJob");
    bean.setTargetMethod("callback");
    bean.setConcurrent(false);
    return bean;
  }

  @Bean(name = "callbackTrigger")
  public CronTriggerFactoryBean callbackTrigger() {
    CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
    bean.setJobDetail(callbackJob().getObject());
    bean.setCronExpression(env.getProperty("remit.callback.cron"));
    bean.setName("callbackTrigger");
    return bean;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    SchedulerFactoryBean bean = new SchedulerFactoryBean();
    bean.setTriggers(payTrigger().getObject(), queryTrigger().getObject(), callbackTrigger().getObject());
    bean.setAutoStartup(false);
    return bean;
  }
}
