/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Sets;

import commons.utils.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@JsonInclude(value = Include.NON_NULL)
public class TransferBatch {

  @JsonIgnore
  private Long id;

  @ApiObjectField(description = "版本号")
  @NotBlank(message = "version is required")
  private String version;

  @ApiObjectField(description = "渠道")
  @NotNull(message = "channel is required")
  private Channel channel;

  @ApiObjectField(description = "签名")
  @NotBlank(message = "sign is required")
  private String sign;

  @ApiObjectField(description = "签名方式")
  @NotNull(message = "signType is required")
  private SignType signType;

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

  private List<String> auditTimes = new ArrayList<>();

  private List<String> auditOpinions = new ArrayList<>();

  private Status status;

  //bank
  @ApiObjectField(description = "出款账号")
  @NotBlank(message = "outAccountId is required")
  private String outAccountId;

  @ApiObjectField(description = "出款账户名")
  private String outAccountName;

  @ApiObjectField(description = "登录用户名")
  private String loginName;

  @ApiObjectField(description = "分行号")
  private BranchCode branchCode;

  @ApiObjectField(description = "业务模式")
  private BusiMode busiMode;

  @ApiObjectField(description = "业务类别")
  private BusiCode busiCode;

  @ApiObjectField(description = "交易类型")
  private TransType transType;

  //since version 1 only supports ￥ so ignore it
  @JsonIgnore
  @ApiObjectField(description = "币种")
  private String currency;

  @ApiObjectField(description = "结算方式")
  private SettleChannel settleChannel;

  //notify
  @JsonIgnore
  private String outTradeNo;

  private Integer successCount;

  private BigDecimal successAmount;

  private String outErrMsg;

  //time stamp
  @JsonIgnore
  private LocalDateTime createTime;

  @JsonIgnore
  private LocalDateTime upDateTime;

  //details
  @ApiObjectField(description = "转账明细")
  @NotNull
  @Size(min = 1, message = "details can not be empty")
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

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public SignType getSignType() {
    return signType;
  }

  public void setSignType(SignType signType) {
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

  public List<String> getAuditTimes() {
    return auditTimes;
  }

  public void setAuditTimes(List<String> auditTimes) {
    this.auditTimes = auditTimes;
  }

  public List<String> getAuditOpinions() {
    return auditOpinions;
  }

  public void setAuditOpinions(List<String> auditOpinions) {
    this.auditOpinions = auditOpinions;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
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

  public BranchCode getBranchCode() {
    return branchCode;
  }

  public void setBranchCode(BranchCode branchCode) {
    this.branchCode = branchCode;
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
    return JsonHelper.toJson(this);
  }

  public static enum SignType {
    MD5(0), SHA(1);

    private int value;

    private SignType(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }

  public static enum Status {

    INIT(0), //
    JUNIOR_REJECTED(1), //
    JUNIOR_APPROVED(2), //
    SENIOR_REJECTED(3), //
    SENIOR_APPROVED(4), //
    FINAL_REJECTED(5), //
    FINAL_APPROVED(6), //
    PROCESSING(7), //
    SUCCESS(8), //
    FAILED(9);

    private int value;

    private Status(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }

    public static final Map<Status, Set<Status>> NEXT_MAP = new HashMap<>();

    static {
      NEXT_MAP.put(INIT, Sets.newHashSet(JUNIOR_APPROVED, JUNIOR_REJECTED));
      NEXT_MAP.put(JUNIOR_APPROVED, Sets.newHashSet(SENIOR_APPROVED, SENIOR_REJECTED, PROCESSING));
      NEXT_MAP.put(SENIOR_APPROVED, Sets.newHashSet(FINAL_APPROVED, FINAL_REJECTED, PROCESSING));
      NEXT_MAP.put(FINAL_APPROVED, Sets.newHashSet(PROCESSING));
      NEXT_MAP.put(PROCESSING, Sets.newHashSet(SUCCESS, FAILED));
    }

    public static boolean isShiftValid(Status from, Status to) {
      Set<Status> nexts = NEXT_MAP.get(from);
      return CollectionUtils.isNotEmpty(nexts) && nexts.contains(to);
    }
  }

  public static enum BusiMode {
    DIRECT_PAYROLL("00001"), //直接代发工资
    CLIENT_PAYROLL("00002"); //客户端代发工资

    private String value;

    private BusiMode(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public static enum BusiCode {
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

    private BusiCode(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public static enum Channel {
    PAY, //支付
    AGENCY; //代发代扣 
  }

  public static enum SettleChannel {
    ORDINARY("N"), FAST("F");

    private String value;

    private SettleChannel(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }
  }

  public static enum BranchCode {
    Root("01"), //
    RootAccounting("03"), //
    Beijing("10"), //
    OffshoreBranch("12"), //
    RootOffshoreCenter("13"), //
    Guangzhou("20"), //
    Shanghai("21"), //
    Tianjin("22"), //
    Chongqing("23"), //
    Shenyang("24"), //
    Nanjing("25"), //
    Wuhan("27"), //
    Chengdu("28"), //
    Xian("29"), //
    Taiyuan("35"), //
    Zhengzhou("37"), //
    Shijiazhuang("38"), //
    Tangshan("39"), //
    Dalian("41"), //
    Changchun("43"), //
    Harbin("45"), //
    Huhehaote("47"), //
    Yinchuan("51"), //
    Suzhou("52"), //
    Qingdao("53"), //
    Ningbo("54"), //
    Hefei("55"), //
    Jinan("56"), //
    Hangzhou("57"), //
    Wenzhou("58"), //
    Fuzhou("59"), //
    Quanzhou("60"), //
    Wuxi("62"), //
    Dongguan("69"), //
    Nanning("71"), //
    Xining("72"), //
    Changsha("73"), //
    Shenzhen("75"), //
    Foshan("77"), //
    Nanchang("79"), //
    Guiyang("85"), //
    Kunming("87"), //
    Haikou("89"), //
    Wulumuqi("91"), //
    Xiamen("92"), //
    Lanzhou("93"), //
    Hongkong("97");

    private String value;

    private BranchCode(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

  }

  public static enum TransType {
    PAYROLL("BYSA", "代发工资"),

    PAY_TRUSTS_RETURNS("BYFD", "代发信托返还资金"),

    PAY_BONUS("BYXI", "代发递延奖金"),

    PAY_COMMUNICATION_FEE("BYXH", "代发通讯费"),

    PAY_TRANSPORTATION_FEE("BYXG", "代发交通费"),

    PAY_TRAVEL_FEE("BYXF", "代发差旅费"),

    PAY_DIVIDEND("BYXE", "代发分红款"),

    PAY_CAR_SUBSIDIES("BYXD", "代发车贴"),

    PAY_HEATING_FEE("BYXC", "代发烤火费"),

    PAY_PUBLIC_FUNDS("BYXB", "代发住房公积金"),

    PAY_LOANS("BYBF", "代发个人贷款"),

    PAY_INSURANCE("BYSU", "代发保险费"),

    PAY_OTHER("BYBK", "代发其他"),

    PAY_LABOR_INCOME("BYBC", "代发劳务收入"),

    PAY_OVERTIME_FEE("BYWK", "代发加班费"),

    PAY_TAXI_FEE("AYCR", "代扣出租车规费"),

    PAY_WELFARE_FEE("BYWF", "代发福利费"),

    PAY_REIMBURSEMENT("BYTF", "代发报销款"),

    OFFSHORE_PAYROLL("BYSS", "离岸代发工资"),

    PAY_FARM_SALES("BYBJ", "代发农副品销售"),

    PAY_TAX_REBATES("BYBI", "代发纳税退还"),

    PAY_INHERITINGS("BYBH", "代发继承赠与款"),

    PAY_RESTRUCTURE_FEE("BYXA", "代发改制费"),

    PAY_FUTURES("BYBG", "代发证券期货款"),

    PAY_CREDITOR_RIGHT_TRANSFER("BYBE", "代发债产权转让"),

    PAY_INVESTMENT_PRINCIPLE_BENEFIT("BYBD", "代发投资本益"),

    WITHHOLD_GAS_FEE("AYGS", "代扣煤气费"),

    WITHHOLD_RENT("AYHS", "代扣房租"),

    WITHHOLD_NET_FEE("AYNT", "代扣上网费"),

    WITHHOLD_TAX("AYTX", "代扣税费"),

    WITHHOLD_OTHER("AYBK", "代扣其他"),

    WITHHOLD_ELECTRICITY_FEE("AYEL", "代扣电费"),

    WITHHOLD_TV_FEE("AYTV", "代扣电视费"),

    WITHOLD_WATER_FEE("AYWT", "代扣水费"),

    WITHHOLD_PHONE_FEE("AYTL", "代扣电话费"),

    WITHHOLD_INSTALMENTS("AYMT", "代扣按揭费"),

    WITHHOLD_SEWAGE_FEE("AYSW", "代扣污水费"),

    WITHHOLD_PROPERTY_FEE("AYPP", "代扣物业管理费"),

    WITHHOLD_MOBILE_FEE("AYMC", "代扣移动电话费"),

    WITHHOLD_CELLPHONE_FEE("AYMB", "代扣手机费"),

    WITHHOLD_TUITION("AYSC", "代扣学费"),

    WITHHOLD_PROPERTY_INSURANCE("AYFS", "代扣财产保险费"),

    WITHHOLD_INSURANCE("AYIS", "代扣保险费"),

    WITHHOLD_OVERUSE_WATER_FEE("AYOW", "代扣超计划用水费"),

    WITHHOLD_INTERESTS("AYCN", "代扣贷款利息"),

    WITHHOLD_LIFE_INSURANCE("AYLS", "代扣人寿保险费"),

    WITHHOLD_CLEANING_FEE("AYCL", "代扣清洁费"),

    WITHHOLD_LOAN_PRINCIPLE_INTEREST("AYLN", "代扣委托贷款本息"),

    WITHHOLD_LOAN_PRINCIPLE("AYCF", "代扣贷款本金"),

    WITHHOLD_RUBBISH_FEE("AYRB", "代扣垃圾费"),

    WITHHOLD_WATER_ELECTRICITY_FEE("AYEW", "代扣水电费"),

    WITHHOLD_BATCH_DEDUCTIONS("AYBT", "代扣批量扣费");

    private String value, message;

    private TransType(String value, String message) {
      this.value = value;
      this.message = message;
    }

    public String getValue() {
      return value;
    }

    public String getMessage() {
      return message;
    }
  }

}
