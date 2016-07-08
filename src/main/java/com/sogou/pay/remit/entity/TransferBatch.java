/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.format.annotation.NumberFormat;

import com.google.common.reflect.TypeToken;
import com.google.gson.annotations.Expose;

import commons.utils.JsonUtil;
import commons.utils.State;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public class TransferBatch {

  private Long id;

  @ApiObjectField(description = "版本号")
  @NotBlank(message = "version is required")
  private String version;

  @ApiObjectField(description = "渠道")
  @NotBlank(message = "channel is required")
  private String channel;

  @ApiObjectField(description = "签名")
  @NotBlank(message = "sign is required")
  private String sign;

  @ApiObjectField(description = "签名方式")
  @NotBlank(message = "signType is required")
  private String signType;

  @ApiObjectField(description = "业务线Id")
  @NotNull(message = "appId is required")
  @Min(value = 1, message = "appId at least be 1")
  @Max(value = 9999, message = "appId at most be 9999")
  private Integer appId;

  @ApiObjectField(description = "批次号")
  @NotBlank(message = "batchNo is required")
  @Size(max = 26, message = "limit of size of batchNo is 26")
  private String batchNo;

  @ApiObjectField(description = "付款笔数")
  private Integer transferCount;

  @ApiObjectField(description = "金额")
  @DecimalMin(value = "0.01", message = "amount at least be 0.01")
  @NumberFormat(pattern = "0.00")
  private BigDecimal transferAmount;

  @ApiObjectField(description = "备注")
  @NotBlank(message = "memo is required")
  private String memo;

  @ApiObjectField(description = "保留字段")
  private String reserve;

  //audit
  private Long auditor;

  private List<LocalDateTime> auditTimeList = new ArrayList<>();

  private List<String> auditOpinionList = new ArrayList<>();

  @Expose(serialize = false)
  private String auditTimes;

  @Expose(serialize = false)
  private String auditOpinions;

  @Expose(serialize = false)
  private Integer status;

  private String statusString;

  //bank
  @ApiObjectField(description = "出款账号")
  @NotBlank(message = "outAccountId is required")
  private String outAccountId;

  @ApiObjectField(description = "出款账户名")
  private String outAccountName;

  @ApiObjectField(description = "登录用户名")
  private String loginName;

  @ApiObjectField(description = "分行号")
  private String branchCode;

  @ApiObjectField(description = "业务模式")
  private String busiMode;

  @ApiObjectField(description = "业务类别")
  private String busiCode;

  @ApiObjectField(description = "交易类型")
  private String transType;

  @ApiObjectField(description = "币种")
  private String currency;

  @ApiObjectField(description = "结算方式")
  private String settleChannel;

  //notify
  @Expose(serialize = false)
  private String outTradeNo;

  private Integer successCount;

  private BigDecimal successAmount;

  private String outErrMsg;

  //time stamp
  @Expose(deserialize = false)
  private LocalDateTime createTime;

  @Expose(deserialize = false)
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

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
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

  public String getReserve() {
    return reserve;
  }

  public void setReserve(String reserve) {
    this.reserve = reserve;
  }

  public Long getAuditor() {
    return auditor;
  }

  public void setAuditor(Long auditor) {
    this.auditor = auditor;
  }

  public List<LocalDateTime> getAuditTimeList() {
    return auditTimeList;
  }

  public void setAuditTimeList() {
    if (StringUtils.isNotBlank(auditTimes))
      this.auditTimeList = JsonUtil.fromJson(auditTimes, TYPE_OF_LOCALDATETIME_LIST);
  }

  public List<String> getAuditOpinionList() {
    return auditOpinionList;
  }

  public void setAuditOpinionList() {
    if (StringUtils.isNotBlank(auditOpinions))
      this.auditOpinionList = JsonUtil.fromJson(auditOpinions, TYPE_OF_STRING_LIST);
  }

  public String getAuditTimes() {
    return auditTimes;
  }

  public void setAuditTimes() {
    this.auditTimes = JsonUtil.toJson(auditTimeList);
  }

  public String getAuditOpinions() {
    return auditOpinions;
  }

  public void setAuditOpinions() {
    this.auditOpinions = JsonUtil.toJson(auditOpinionList);
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getStatusString() {
    return statusString;
  }

  public void setStatusString(String statusString) {
    this.statusString = statusString;
  }

  public String getOutAccountId() {
    return outAccountId;
  }

  public void setOutAccountId(String outAccountId) {
    this.outAccountId = outAccountId;
  }

  public String getOutAccountName() {
    return outAccountName;
  }

  public void setOutAccountName(String outAccountName) {
    this.outAccountName = outAccountName;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getBranchCode() {
    return branchCode;
  }

  public void setBranchCode(String branchCode) {
    this.branchCode = branchCode;
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

  @Override
  public String toString() {
    return JsonUtil.toJson(this);
  }

  public TransferBatch setString() {
    setAuditOpinions();
    setAuditTimes();
    return this;
  }

  public TransferBatch setList() {
    setAuditOpinionList();
    setAuditTimeList();
    return this;
  }

  private static final Type TYPE_OF_LOCALDATETIME_LIST = new TypeToken<List<LocalDateTime>>() {}.getType(),
      TYPE_OF_STRING_LIST = new TypeToken<List<String>>() {}.getType();

  public static class Status {

    public static final State<Integer> INIT = new State<>(1), JUNIOR_APPROVED = new State<>(2),
        JUNIOR_REJECTED = new State<>(3), SENIOR_APPROVED = new State<>(4), SENIOR_REJECTED = new State<>(5),
        FINAL_APPROVED = new State<>(6), FINAL_REJECTED = new State<>(7), PROCESSING = new State<>(8),
        FAILED = new State<>(9), SUCCESS = new State<>(10), NOTIFIED = new State<>(11);

    public static final Map<Integer, State<Integer>> STATE_MAP = new HashMap<>();

    static {
      INIT.addNexts(Arrays.asList(JUNIOR_APPROVED, JUNIOR_REJECTED));
      JUNIOR_APPROVED.addPrevious(Arrays.asList(INIT))
          .addNexts(Arrays.asList(SENIOR_APPROVED, SENIOR_REJECTED, PROCESSING));
      JUNIOR_REJECTED.addPrevious(Arrays.asList(INIT));
      SENIOR_APPROVED.addPrevious(Arrays.asList(JUNIOR_APPROVED))
          .addNexts(Arrays.asList(FINAL_APPROVED, FINAL_REJECTED, PROCESSING));
      SENIOR_REJECTED.addPrevious(Arrays.asList(JUNIOR_APPROVED));
      FINAL_APPROVED.addPrevious(Arrays.asList(SENIOR_APPROVED)).addNexts(Arrays.asList(PROCESSING));
      FINAL_REJECTED.addPrevious(Arrays.asList(SENIOR_APPROVED));
      PROCESSING.addPrevious(Arrays.asList(JUNIOR_APPROVED, SENIOR_APPROVED, FINAL_APPROVED))
          .addNexts(Arrays.asList(SUCCESS, FAILED));
      SUCCESS.addPrevious(Arrays.asList(PROCESSING)).addNexts(Arrays.asList(NOTIFIED));
      FAILED.addPrevious(Arrays.asList(PROCESSING)).addNexts(Arrays.asList(NOTIFIED));
      NOTIFIED.addPrevious(Arrays.asList(SUCCESS, FAILED));

      Arrays.asList(INIT, JUNIOR_APPROVED, JUNIOR_REJECTED, SENIOR_APPROVED, SENIOR_REJECTED, FINAL_APPROVED,
          FINAL_REJECTED, PROCESSING, FAILED, SUCCESS, NOTIFIED).forEach(s -> STATE_MAP.put(s.getValue(), s));
    }

    public static boolean isShiftValid(Integer from, Integer to) {
      State<Integer> fromState = STATE_MAP.get(from);
      return fromState != null && fromState.getNexts().contains(STATE_MAP.get(to));
    }
  }

}
