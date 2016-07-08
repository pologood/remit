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
  /*支付*/
  PAY("N02030"), //支付
  DIRECT_PAY("N02031"), //直接支付
  GROUP_PAY("N02040"), //集团支付
  DIRECT_GROUP_PAY("N02041"), //直接集团支付

  /*代发代扣 */
  PAYROLL("N03010"), //代发工资
  AGENT_PAY("N03020"), //代发
  WITHHOLD("N03030");//代扣

  private String value;

  private BusiType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
