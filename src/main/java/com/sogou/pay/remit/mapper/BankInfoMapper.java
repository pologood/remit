/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;

import com.sogou.pay.remit.entity.BankInfo;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
public interface BankInfoMapper {

  /**
   * @return
   */
  List<BankInfo> list();

}
