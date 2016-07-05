package com.sogou.pay.remit.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProjectInfo {
  public static final String PKG_PREFIX    = "com.sogou.pay.remit";
  public static final String API_PKG       = "com.sogou.pay.remit.api";
  public static final String MAPPER_PKG    = "com.sogou.pay.remit.mapper";
  public static final List<String> DOC_PKG = Collections.unmodifiableList(
    Arrays.asList("com.sogou.pay.remit.api", "com.sogou.pay.remit.entity", "com.sogou.pay.remit.model"));
}
