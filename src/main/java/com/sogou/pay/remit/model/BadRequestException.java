/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.model;

import com.sogou.pay.remit.enums.Exceptions;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月5日;
//-------------------------------------------------------
public class BadRequestException extends RuntimeException {

  public BadRequestException(Exceptions exception) {
    super(exception.getErrMsg());
  }

  public BadRequestException(String message) {
    super(message);
  }

}
