/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public enum TransType {
  BYSA("代发工资"), BYBK("代发其他");

  private String value;

  private TransType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
