/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import commons.utils.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@ApiObject(name = "TransferDetail", description = "明细单", group = "TransferDetail")
public class TransferDetail {

  @JsonIgnore
  private Long id;

  private Integer appId;

  private String batchNo;

  @ApiObjectField(description = "转账流水号", required = true)
  @NotBlank(message = "transferId is required")
  private String transferId;

  @ApiObjectField(description = "入款账号", required = true)
  @NotBlank(message = "inAccountId is required")
  private String inAccountId;

  @ApiObjectField(description = "入款账户名", required = true)
  @NotBlank(message = "inAccountName is required")
  private String inAccountName;

  @ApiObjectField(description = "金额", required = true)
  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount at least be 0.01")
  @NumberFormat(pattern = "0.00")
  private BigDecimal amount;

  @ApiObjectField(description = "他行名")
  private String bankName;

  @ApiObjectField(description = "他行地址")
  private String bankCity;

  @ApiObjectField(description = "备注")
  private String memo;

  @JsonProperty(access = Access.READ_ONLY)
  private Status status;

  @JsonProperty(access = Access.READ_ONLY)
  private String outErrMsg;

  //time stamp
  @JsonIgnore
  private LocalDateTime updateTime;

  @ApiObject(name = "Detail.Status", description = "明细状态", group = "TransferDetail")
  public enum Status {

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

  @Override
  public String toString() {
    return JsonHelper.toJson(this);
  }

}
