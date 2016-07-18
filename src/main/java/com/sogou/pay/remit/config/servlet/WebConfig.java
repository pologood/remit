package com.sogou.pay.remit.config.servlet;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sogou.pay.remit.api.SignInterceptor;
import com.sogou.pay.remit.config.ProjectInfo;

import commons.utils.LocalDateJsonSerializer;
import commons.utils.LocalDateTimeJsonSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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

  @Bean
  public MappedInterceptor signInterceptor() {
    return new MappedInterceptor(new String[] { "/api/transferBatch" }, new SignInterceptor());
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    stringConverter.setWriteAcceptCharset(false);
    converters.add(stringConverter);

    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    builder.serializationInclusion(Include.NON_NULL);
    builder.serializationInclusion(Include.NON_EMPTY);
    builder.serializerByType(LocalDateTime.class, new LocalDateTimeJsonSerializer());
    builder.serializerByType(LocalDate.class, new LocalDateJsonSerializer());
    converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
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
