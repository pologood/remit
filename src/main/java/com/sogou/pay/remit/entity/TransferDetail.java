/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.ApiObjectField;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public class TransferDetail {

  private Long id;

  private Integer appId;

  private String batchNo;

  @ApiObjectField(description = "转账流水号")
  @NotNull(message = "transferId is required")
  private String transferId;

  @ApiObjectField(description = "入款账户名")
  @NotNull(message = "transferId is required")
  private String inAcountName;

  @ApiObjectField(description = "入款账号")
  @NotNull(message = "transferId is required")
  private String inAccountId;

  @ApiObjectField(description = "金额")
  @NotNull(message = "transferId is required")
  private BigDecimal amount;

  private String bankName;

  private String bankCity;

  private String memo;

  //
  private Integer status;

  private String outErrMsg;

  //time stamp
  private LocalDateTime createTime;

  private LocalDateTime updateTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getAppId() {
    return appId;
  }

  public void setAppId(Integer appId) {
    this.appId = appId;
  }

  public String getBatchNo() {
    return batchNo;
  }

  public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
  }

  public String getTransferId() {
    return transferId;
  }

  public void setTransferId(String transferId) {
    this.transferId = transferId;
  }

  public String getInAcountName() {
    return inAcountName;
  }

  public void setInAcountName(String inAcountName) {
    this.inAcountName = inAcountName;
  }

  public String getInAccountId() {
    return inAccountId;
  }

  public void setInAccountId(String inAccountId) {
    this.inAccountId = inAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getBankCity() {
    return bankCity;
  }

  public void setBankCity(String bankCity) {
    this.bankCity = bankCity;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getOutErrMsg() {
    return outErrMsg;
  }

  public void setOutErrMsg(String outErrMsg) {
    this.outErrMsg = outErrMsg;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

}
