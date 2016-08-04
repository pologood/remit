package com.sogou.pay.remit.config.servlet;

import com.sogou.pay.remit.api.AdminInterceptor;
import com.sogou.pay.remit.api.FinalInterceptor;
import com.sogou.pay.remit.api.LogInterceptor;
import com.sogou.pay.remit.api.SignInterceptor;
import com.sogou.pay.remit.config.ProjectInfo;

import commons.utils.JsonHelper;

import java.io.IOException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebMvc
@ComponentScan({ ProjectInfo.API_PKG })
public class WebConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private FinalInterceptor finalInterceptor;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    stringConverter.setWriteAcceptCharset(false);
    converters.add(stringConverter);
    converters.add(new MappingJackson2HttpMessageConverter(JsonHelper.MAPPER));
  }

  @Bean
  public MultipartResolver multipartResolver() throws IOException {
    return new StandardServletMultipartResolver();
  }

  @Bean
  public RequestMappingHandlerMapping requestMappingHandlerMapping() {
    RequestMappingHandlerMapping r = new RequestMappingHandlerMapping();
    r.setUseTrailingSlashMatch(false);
    r.setUseSuffixPatternMatch(false);
    r.setRemoveSemicolonContent(false);
    r.setInterceptors(
        new Object[] { new MappedInterceptor(new String[] { "/api/transferBatch" }, new SignInterceptor()),
            new MappedInterceptor(
                new String[] { "/api/transferDetail", "/api/transferBatch/**", "/api/job", "/api/job/**", "/api/user" },
                new LogInterceptor()),
        new MappedInterceptor(new String[] { "/api/job", "/api/job/**", "/api/refresh" }, new AdminInterceptor()),
        new MappedInterceptor(new String[] { "/api/user" }, finalInterceptor) });
    return r;
  }
}
