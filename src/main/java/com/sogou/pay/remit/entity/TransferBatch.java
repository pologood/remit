/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jsondoc.core.annotation.ApiObjectField;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public class TransferBatch {

  private Long id;

  @ApiObjectField(description = "版本号")
  @NotNull(message = "version is required")
  private String version;

  @ApiObjectField(description = "签名")
  @NotNull(message = "sign is required")
  private String sign;

  @ApiObjectField(description = "签名方式")
  @NotNull(message = "signType is required")
  private String signType;

  @ApiObjectField(description = "业务线Id")
  @Min(value = 1, message = "appId must be greater than 0")
  private Integer appId;

  @ApiObjectField(description = "批次号")
  @NotNull(message = "batchNo is required")
  @Size(max = 32, message = "limit of size of batchNo is 32")
  private String batchNo;

  private String transferId;

  @ApiObjectField(description = "出款账户名")
  @NotNull(message = "outAccountName is required")
  private String outAccountName;

  @ApiObjectField(description = "出款账号")
  @NotNull(message = "outAccountId is required")
  private String outAccountId;

  @ApiObjectField(description = "分行号")
  @Min(value = 1, message = "branchCode must be greater than 0")
  private Integer branchCode;

  private Integer transferCount;

  private BigDecimal transferAmount;

  @ApiObjectField(description = "备注")
  @NotNull(message = "memo is required")
  private String memo;

  //audit
  private Long auditor;

  private List<LocalDateTime> auditTimes;

  private List<String> auditOpinions;

  private Integer status;

  //bank
  private String busiCode;

  private String busiMode;

  private String transType;

  //notify
  private String outTradeNo;

  private Integer successCount;

  private BigDecimal successAmount;

  private String outErrMsg;

  //time stamp
  private LocalDateTime createTime;

  private LocalDateTime upDateTime;

  //details
  @ApiObjectField(description = "转账明细")
  @NotNull(message = "details is required")
  @Size(min = 1, message = "details must not be empty")
  private List<TransferDetail> details;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getSignType() {
    return signType;
  }

  public void setSignType(String signType) {
    this.signType = signType;
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

  public String getOutAccountName() {
    return outAccountName;
  }

  public void setOutAccountName(String outAccountName) {
    this.outAccountName = outAccountName;
  }

  public String getOutAccountId() {
    return outAccountId;
  }

  public void setOutAccountId(String outAccountId) {
    this.outAccountId = outAccountId;
  }

  public Integer getBranchCode() {
    return branchCode;
  }

  public void setBranchCode(Integer branchCode) {
    this.branchCode = branchCode;
  }

  public Integer getTransferCount() {
    return transferCount;
  }

  public void setTransferCount(Integer transferCount) {
    this.transferCount = transferCount;
  }

  public BigDecimal getTransferAmount() {
    return transferAmount;
  }

  public void setTransferAmount(BigDecimal transferAmount) {
    this.transferAmount = transferAmount;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getAuditor() {
    return auditor;
  }

  public void setAuditor(Long auditor) {
    this.auditor = auditor;
  }

  public List<LocalDateTime> getAuditTimes() {
    return auditTimes;
  }

  public void setAuditTimes(List<LocalDateTime> auditTimes) {
    this.auditTimes = auditTimes;
  }

  public List<String> getAuditOpinions() {
    return auditOpinions;
  }

  public void setAuditOpinions(List<String> auditOpinions) {
    this.auditOpinions = auditOpinions;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getBusiCode() {
    return busiCode;
  }

  public void setBusiCode(String busiCode) {
    this.busiCode = busiCode;
  }

  public String getBusiMode() {
    return busiMode;
  }

  public void setBusiMode(String busiMode) {
    this.busiMode = busiMode;
  }

  public String getTransType() {
    return transType;
  }

  public void setTransType(String transType) {
    this.transType = transType;
  }

  public String getOutTradeNo() {
    return outTradeNo;
  }

  public void setOutTradeNo(String outTradeNo) {
    this.outTradeNo = outTradeNo;
  }

  public Integer getSuccessCount() {
    return successCount;
  }

  public void setSuccessCount(Integer successCount) {
    this.successCount = successCount;
  }

  public BigDecimal getSuccessAmount() {
    return successAmount;
  }

  public void setSuccessAmount(BigDecimal successAmount) {
    this.successAmount = successAmount;
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

  public LocalDateTime getUpDateTime() {
    return upDateTime;
  }

  public void setUpDateTime(LocalDateTime upDateTime) {
    this.upDateTime = upDateTime;
  }

  public List<TransferDetail> getDetails() {
    return details;
  }

  public void setDetails(List<TransferDetail> details) {
    this.details = details;
  }

}
