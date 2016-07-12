/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.model;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月5日;
//-------------------------------------------------------
public class InternalErrorException extends RuntimeException {

  public InternalErrorException(String message) {
    super(message);
  }

  public InternalErrorException(Throwable throwable) {
    super(throwable);
  }
}
