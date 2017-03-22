package com.sogou.pay.remit.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccountDetail {
  private LocalDate tradeDate;
  private LocalTime tradeTime;
  private LocalDate interestDate;
  private TradeType tradeType;
  private String abstracts;
  private BigDecimal debitAmount;
  private BigDecimal creditAmount;
  private CreditOrDebit flag;
  private BigDecimal balance;
  private String serialNumber;
  private Integer instanceNo;
  private String businessName;
  private String usage;
  private String businessNo;
  private String businessAbstract;
  private String otherAbstract;
  private String accountRegion;
  private String accountName;
  private String accountId;
  private String bankCode;
  private String bankName;
  private String bankLocation;
  private String companyRegion;
  private String companyAccount;
  private String companyName;
  private String informationFlag;
  private String hasAttachmentFlag;
  private String invoice;
  private String fixFlag;
  private String extAbstract;
  private String tradeCode;
  private String merchantOrderId;
  private String companyCode;

  public LocalDate getTradeDate() {
    return tradeDate;
  }

  public void setTradeDate(LocalDate tradeDate) {
    this.tradeDate = tradeDate;
  }

  public LocalTime getTradeTime() {
    return tradeTime;
  }

  public void setTradeTime(LocalTime tradeTime) {
    this.tradeTime = tradeTime;
  }

  public LocalDate getInterestDate() {
    return interestDate;
  }

  public void setInterestDate(LocalDate interestDate) {
    this.interestDate = interestDate;
  }

  public TradeType getTradeType() {
    return tradeType;
  }

  public void setTradeType(TradeType tradeType) {
    this.tradeType = tradeType;
  }

  public String getAbstracts() {
    return abstracts;
  }

  public void setAbstracts(String abstracts) {
    this.abstracts = abstracts;
  }

  public BigDecimal getDebitAmount() {
    return debitAmount;
  }

  public void setDebitAmount(BigDecimal debitAmount) {
    this.debitAmount = debitAmount;
  }

  public BigDecimal getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(BigDecimal creditAmount) {
    this.creditAmount = creditAmount;
  }

  public CreditOrDebit getFlag() {
    return flag;
  }

  public void setFlag(CreditOrDebit flag) {
    this.flag = flag;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public Integer getInstanceNo() {
    return instanceNo;
  }

  public void setInstanceNo(Integer instanceNo) {
    this.instanceNo = instanceNo;
  }

  public String getBusinessName() {
    return businessName;
  }

  public void setBusinessName(String businessName) {
    this.businessName = businessName;
  }

  public String getUsage() {
    return usage;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public String getBusinessNo() {
    return businessNo;
  }

  public void setBusinessNo(String businessNo) {
    this.businessNo = businessNo;
  }

  public String getBusinessAbstract() {
    return businessAbstract;
  }

  public void setBusinessAbstract(String businessAbstract) {
    this.businessAbstract = businessAbstract;
  }

  public String getOtherAbstract() {
    return otherAbstract;
  }

  public void setOtherAbstract(String otherAbstract) {
    this.otherAbstract = otherAbstract;
  }

  public String getAccountRegion() {
    return accountRegion;
  }

  public void setAccountRegion(String accountRegion) {
    this.accountRegion = accountRegion;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getBankCode() {
    return bankCode;
  }

  public void setBankCode(String bankCode) {
    this.bankCode = bankCode;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getBankLocation() {
    return bankLocation;
  }

  public void setBankLocation(String bankLocation) {
    this.bankLocation = bankLocation;
  }

  public String getCompanyRegion() {
    return companyRegion;
  }

  public void setCompanyRegion(String companyRegion) {
    this.companyRegion = companyRegion;
  }

  public String getCompanyAccount() {
    return companyAccount;
  }

  public void setCompanyAccount(String companyAccount) {
    this.companyAccount = companyAccount;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getInformationFlag() {
    return informationFlag;
  }

  public void setInformationFlag(String informationFlag) {
    this.informationFlag = informationFlag;
  }

  public String getHasAttachmentFlag() {
    return hasAttachmentFlag;
  }

  public void setHasAttachmentFlag(String hasAttachmentFlag) {
    this.hasAttachmentFlag = hasAttachmentFlag;
  }

  public String getInvoice() {
    return invoice;
  }

  public void setInvoice(String invoice) {
    this.invoice = invoice;
  }

  public String getFixFlag() {
    return fixFlag;
  }

  public void setFixFlag(String fixFlag) {
    this.fixFlag = fixFlag;
  }

  public String getExtAbstract() {
    return extAbstract;
  }

  public void setExtAbstract(String extAbstract) {
    this.extAbstract = extAbstract;
  }

  public String getTradeCode() {
    return tradeCode;
  }

  public void setTradeCode(String tradeCode) {
    this.tradeCode = tradeCode;
  }

  public String getMerchantOrderId() {
    return merchantOrderId;
  }

  public void setMerchantOrderId(String merchantOrderId) {
    this.merchantOrderId = merchantOrderId;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }

  public enum CreditOrDebit {
    CREDIT("C"), DEBIT("D");

    private final String value;

    private CreditOrDebit(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    private static final Map<String, CreditOrDebit> MAP = new HashMap<>();

    static {
      Arrays.stream(CreditOrDebit.values()).forEach(flag -> MAP.put(flag.getValue(), flag));
    }

    public static CreditOrDebit get(String value) {
      return MAP.get(value);
    }
  }

  public enum TradeType {
    OUT("CPAA", "对公转账正常提出"),

    REFUND("CPAB", "对公转账被支付平台退回"),

    CANCEL_CUSTOMER("CPAC", "对公转账客户撤销"),

    CANCEL_INTERNAL("CPAD", "对公转账内部撤销"),

    RETRY("CPAE", "对公转账被支付平台退回后重新提出"),

    BACK_INCOME("CPAF", "支付平台退回的业务直接入客户账"),

    PUBLIC_SUSPEND("CPAG", "支付平台退回的业务挂账"),

    AUDIT("CPAH", "企业银行业务待银行审核"),

    REJECT("CPAI", "企业银行业务银行审核不通过驳回"),

    BACK("CPAJ", "企业银行和应急模式业务被支付平台退回"),

    CREDIT_SUSPEND("CPCA", "支票圈存挂账"),

    INCOME("CPUA", "对公提回贷记入款"),

    OUTCOME("CPUB", "对公提回借记出款"),

    FIX("CPUC", "对公提回借记冲销");

    private final String value, description;

    private TradeType(String value, String description) {
      this.value = value;
      this.description = description;
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }

    static final Map<String, TradeType> TYPE_MAP = new HashMap<>();

    static {
      Arrays.stream(TradeType.values()).forEach(type -> TYPE_MAP.put(type.getValue(), type));
    }

    public static TradeType get(String type) {
      return TYPE_MAP.get(type);
    }
  }
}
