package com.sogou.pay.remit.config.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.google.gson.GsonBuilder;
import com.sogou.pay.remit.config.ProjectInfo;

import commons.utils.LocalDateJsonConverter;
import commons.utils.LocalDateTimeJsonConverter;

@Configuration
@EnableWebMvc
@ComponentScan({ ProjectInfo.API_PKG })
public class WebConfig extends WebMvcConfigurerAdapter {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    stringConverter.setWriteAcceptCharset(false);
    converters.add(stringConverter);

    GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat(DATE_TIME_FORMAT);
    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateJsonConverter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter());
    gsonConverter.setGson(gsonBuilder.create());
    converters.add(gsonConverter);
  }

  @Bean
  public MultipartResolver multipartResolver() throws IOException {
    return new StandardServletMultipartResolver();
  }

  @Bean
  public RequestMappingHandlerMapping requestMappingHandlerMapping() {
    RequestMappingHandlerMapping r = new RequestMappingHandlerMapping();
    r.setRemoveSemicolonContent(false);
    return r;
  }
}
