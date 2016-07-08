/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
public class BankInfo {

  private Long id;

  private String channel;

  private String accountId;

  private String accountName;

  private String loginName;

  private String busiMode;

  private String busiCode;

  private String transType;

  private String branchCode;

  private String currency;

  private String settleChannel;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getBusiMode() {
    return busiMode;
  }

  public void setBusiMode(String busiMode) {
    this.busiMode = busiMode;
  }

  public String getBusiCode() {
    return busiCode;
  }

  public void setBusiCode(String busiCode) {
    this.busiCode = busiCode;
  }

  public String getTransType() {
    return transType;
  }

  public void setTransType(String transType) {
    this.transType = transType;
  }

  public String getBranchCode() {
    return branchCode;
  }

  public void setBranchCode(String branchCode) {
    this.branchCode = branchCode;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getSettleChannel() {
    return settleChannel;
  }

  public void setSettleChannel(String settleChannel) {
    this.settleChannel = settleChannel;
  }

}
