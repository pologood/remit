package com.sogou.pay.remit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import commons.spring.CodeAutoGen;

@Configuration
public class AutoCodeConfig {

  @Bean(name = "autoCodeController")
  public CodeAutoGen autoCodeController() {
    return new CodeAutoGen();
  }
}
