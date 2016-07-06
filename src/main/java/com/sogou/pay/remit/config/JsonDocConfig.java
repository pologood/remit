package com.sogou.pay.remit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import commons.jsondoc.Spring4xJSONDocScanner;
import commons.jsondoc.JsonDocController;

@Configuration
public class JsonDocConfig {
  @Bean(name = "documentationController")
  public JsonDocController jsonDocController() {
    JsonDocController c = new JsonDocController("1.0", "", ProjectInfo.DOC_PKG);
    c.setJsonDocScanner(new Spring4xJSONDocScanner());
    return c;
  }
  
  static class person{
    String name="test name",sex;
  }
  
  public static void main(String[] args){
  
    System.out.println(new Gson().toJson(new person()));
  }
}
