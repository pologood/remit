package com.sogou.pay.remit.enums;
public enum Exceptions {

  SUCCESS(0, "SUCCESS", "成功"),

  ORDERID_INVALID(100001, "ORDERID_INVALID", "订单信息不正确"),

  ORDERSTATUS_INVALID(100002, "ORDERSTATUS_INVALID", "订单状态不正确"),

  QRCODE_EXPIRED(100003, "QRCODE_EXPIRED", "二维码失效"),

  SIGN_INVALID(100004, "SIGN_INVALID", "签名错误");

  private int errorCode;

  private String errorMsg;

  private String chineseMsg;

  Exceptions(int errorCode, String errorMsg, String chineseMsg) {
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
    this.chineseMsg = chineseMsg;
  }

  /**
   * 获取错误码code
   *
   * @return String
   */
  public int getErrorCode() {
    return this.errorCode;
  }

  /**
   * 获取错误信息
   *
   * @return String
   */
  public String getErrorMsg() {
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