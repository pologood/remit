/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public enum BusiMode {
  DIRECT_PAYROLL("00001"), //直接代发工资
  CLIENT_PAYROLL("00002"); //客户端代发工资

  private String value;

  private BusiMode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
