/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public enum TransferStatus {
  INIT(1),

  JUNIOR_APPROVED(2), JUNIOR_REJECTED(3),

  SENIOR_APPROVED(4), SENIOR_REJECTED(5),

  FINAL_APPROVED(6), FINAL_REJECTED(7),

  PROCESSING(8), FAILED(9), SUCCESS(10), NOTIFIED(11);

  private int value;

  TransferStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
