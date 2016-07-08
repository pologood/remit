/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public enum TransType {
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

  private String value;

  private String message;

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
