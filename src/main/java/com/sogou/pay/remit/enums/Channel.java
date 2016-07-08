/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.sogou.pay.remit.entity.TransferBatch;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
public enum Channel {

  PAY, //支付
  AGENCY; //代发代扣 

  public static Map<String, Channel> CHANNEL_MAP = new HashMap<>();

  static {
    Arrays.stream(Channel.values()).forEach(c -> CHANNEL_MAP.put(c.name(), c));
  }
  
  public static Channel getChannel(TransferBatch batch){
    return getChannel(batch.getChannel());
  }

  public static Channel getChannel(String name) {
    return CHANNEL_MAP.get(name);
  }

}
