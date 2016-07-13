/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import com.sogou.pay.remit.entity.TransferBatch.BranchCode;
import com.sogou.pay.remit.entity.TransferBatch.BusiCode;
import com.sogou.pay.remit.entity.TransferBatch.BusiMode;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.SettleChannel;
import com.sogou.pay.remit.entity.TransferBatch.TransType;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
public class BankInfo {

  private Long id;

  private Channel channel;

  private String accountId;

  private String accountName;

  private String loginName;

  private BusiMode busiMode;

  private BusiCode busiCode;

  private TransType transType;

  private BranchCode branchCode;

  private String currency;

  private SettleChannel settleChannel;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
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

  public BusiMode getBusiMode() {
    return busiMode;
  }

  public void setBusiMode(BusiMode busiMode) {
    this.busiMode = busiMode;
  }

  public BusiCode getBusiCode() {
    return busiCode;
  }

  public void setBusiCode(BusiCode busiCode) {
    this.busiCode = busiCode;
  }

  public TransType getTransType() {
    return transType;
  }

  public void setTransType(TransType transType) {
    this.transType = transType;
  }

  public BranchCode getBranchCode() {
    return branchCode;
  }

  public void setBranchCode(BranchCode branchCode) {
    this.branchCode = branchCode;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public SettleChannel getSettleChannel() {
    return settleChannel;
  }

  public void setSettleChannel(SettleChannel settleChannel) {
    this.settleChannel = settleChannel;
  }

}
