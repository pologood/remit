/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public enum BusiType {
  PAYROLL("N03010"), //代发工资
  PAYMENT("N03020"), //代发
  WITHHOLD("N03030");//代扣

  private String value;

  private BusiType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
