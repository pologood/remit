package com.sogou.pay.remit.config;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import redis.clients.jedis.JedisPool;

import commons.saas.RestNameService;
import commons.spring.LoggerFilter;
import commons.spring.LooseGsonHttpMessageConverter;
import commons.spring.MultiReadableFilter;
import commons.spring.RestTemplateFilter;
import commons.spring.XssFilter;

@Configuration
@EnableScheduling
@ComponentScan({ ProjectInfo.PKG_PREFIX + ".api", ProjectInfo.PKG_PREFIX + ".manager",
    ProjectInfo.PKG_PREFIX + ".job" })
@PropertySource(value = "classpath:application-dev.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
public class RootConfig {

  @Autowired
  Environment env;

  @Bean
  public MBeanServerFactoryBean mbeanServer() {
    MBeanServerFactoryBean mbeanServer = new MBeanServerFactoryBean();
    mbeanServer.setDefaultDomain(ProjectInfo.PKG_PREFIX);
    mbeanServer.setLocateExistingServerIfPossible(true);
    return mbeanServer;
  }

  @Bean
  public AnnotationMBeanExporter annotationMBeanExporter() {
    return new AnnotationMBeanExporter();
  }

  @Bean
  public LoggerFilter loggerFilter() {
    return new LoggerFilter(env);
  }

  @Bean
  public XssFilter xssFilter() {
    return new XssFilter(env);
  }

  @Bean
  public MultiReadableFilter multiReadableFilter() {
    return new MultiReadableFilter();
  }

  @Bean
  public JedisPool jedisPool() {
    return new JedisPool(env.getRequiredProperty("redis.url"), env.getRequiredProperty("redis.port", Integer.class));
  }

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(Integer.parseInt(env.getProperty("threadpool.max", "1")));
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    return scheduler;
  }

  @Bean
  public RestNameService restNameService() {
    return new RestNameService(env);
  }

  private String timeOut = Long.toString(TimeUnit.SECONDS.toMillis(60));

  @Bean
  public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(Integer.parseInt(env.getProperty("rest.timeout.connect", timeOut)));
    factory.setReadTimeout(Integer.parseInt(env.getProperty("rest.timeout.read", timeOut)));
    return factory;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate rest = new RestTemplate(httpComponentsClientHttpRequestFactory());
    rest.setInterceptors(Arrays.asList(new RestTemplateFilter()));
    rest.getMessageConverters().add(new LooseGsonHttpMessageConverter());
    return rest;
  }

  @Bean
  public RestTemplate restTemplateGBK() {
    Charset gbk = Charset.forName("GBK");
    RestTemplate rest = new RestTemplate(httpComponentsClientHttpRequestFactory());
    rest.setInterceptors(Arrays.asList(new RestTemplateFilter(gbk)));
    rest.getMessageConverters().add(0, new StringHttpMessageConverter(gbk));
    rest.getMessageConverters().add(1, new LooseGsonHttpMessageConverter(gbk));
    return rest;
  }

}
