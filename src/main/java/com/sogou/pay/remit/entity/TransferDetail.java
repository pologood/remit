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

import com.fasterxml.jackson.annotation.JsonIgnore;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public class TransferDetail {

  @JsonIgnore
  private Long id;

  @JsonIgnore
  private Integer appId;

  @JsonIgnore
  private String batchNo;

  @ApiObjectField(description = "转账流水号")
  @NotNull(message = "transferId is required")
  private String transferId;

  @ApiObjectField(description = "入款账户名")
  @NotNull(message = "inAccountName is required")
  private String inAccountName;

  @ApiObjectField(description = "入款账号")
  @NotNull(message = "inAccountId is required")
  private String inAccountId;

  @ApiObjectField(description = "金额")
  @NotNull(message = "amount is required")
  private BigDecimal amount;

  private String bankName;

  private String bankCity;

  private String memo;

  //
  private Status status;

  private String outErrMsg;

  //time stamp
  @JsonIgnore
  private LocalDateTime updateTime;

  public static enum Status {

    INIT(0), OUT_INIT(1), SUCCESS(2), FAILED(3), CANCELED(4);

    private int value;

    private Status(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }

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

  public String getInAccountName() {
    return inAccountName;
  }

  public void setInAccountName(String inAccountName) {
    this.inAccountName = inAccountName;
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getOutErrMsg() {
    return outErrMsg;
  }

  public void setOutErrMsg(String outErrMsg) {
    this.outErrMsg = outErrMsg;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

}
