package com.sogou.pay.remit.enums;

public enum Exceptions {

  SUCCESS("000000", "SUCCESS", "成功"),

  DATA_PERSISTENCE_FAILED("000001", "DATA_PERSISTENCE_FAILED", "数据持久化失败"),

  ENTITY_NOT_FOUND("000002", "ENTITY_NOT_FOUND", "查无此数据"),

  COMMUNICATION_FAILURE("000003", "COMMUNICATION_FAILURE", "外部通信失败"),

  CHANNEL_INVALID("100001", "CHANNEL_INVALID", "无效的渠道"),

  APPID_INVALID("100002", "APPID_INVALID", "无效的业务线"),

  BATCHNO_INVALID("100003", "BATCHNO_INVALID", "无效的批次号"),

  SINGTYPE_INVALID("100004", "SINGTYPE_INVALID", "无效的签名方式"),

  SIGN_INVALID("100005", "SIGN_INVALID", "签名错误"),

  STATUS_INVALID("100006", "STATUS_INVALID", "非法改变状态"),

  AMOUNT_INVALID("100007", "AMOUNT_INVALID", "无效的金额"),

  BANKINFO_NOT_FOUND("100008", "BANKINFO_NOT_FOUND", "无对应账号信息"),

  DETAIL_INVALID("100009", "DETAIL_INVALID", "无效的明细信息"),

  JOB_INVALID("100010", "JOB_INVALID", "无效的定时任务"),
  
  CRON_INVALID("100011", "CRON_INVALID", "无效的定时表达式");

  private String errorCode;

  private String errorMsg;

  private String chineseMsg;

  Exceptions(String errorCode, String errorMsg, String chineseMsg) {
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
    this.chineseMsg = chineseMsg;
  }

  /**
   * 获取错误码code
   *
   * @return String
   */
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * 获取错误信息
   *
   * @return String
   */
  public String getErrMsg() {
    return this.errorMsg;
  }

  /**
   * 获取中文错误信息
   *
   * @return
   */
  public String getChineseMsg() {
    return chineseMsg;
  }

}
