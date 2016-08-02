package com.sogou.pay.remit.config.servlet;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.sogou.pay.remit.config.AutoCodeConfig;
import com.sogou.pay.remit.config.CronConfig;
import com.sogou.pay.remit.config.DaoConfig;
import com.sogou.pay.remit.config.JsonDocConfig;
import com.sogou.pay.remit.config.RootConfig;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class<?>[] { RootConfig.class, DaoConfig.class, CronConfig.class };
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class<?>[] { WebConfig.class, JsonDocConfig.class, AutoCodeConfig.class };
  }

  @Override
  protected Filter[] getServletFilters() {
    return new Filter[] { new DelegatingFilterProxy("loggerFilter"), new DelegatingFilterProxy("xssFilter"),
        new DelegatingFilterProxy("multiReadableFilter") };
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] { "/" };
  }

  @Override
  protected void customizeRegistration(Dynamic registration) {
    registration.setMultipartConfig(new MultipartConfigElement("/tmp", 2500_000, 2500_000, 2500_000));
  }
}
