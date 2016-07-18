/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.entity.BankInfo;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.BankInfoMapper;
import com.sogou.pay.remit.model.BadRequestException;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
@Component
public class BankInfoManager implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankInfoManager.class);

  private static Map<String, BankInfo> AGENCY_MAP = new HashMap<>();

  private static Map<String, BankInfo> PAY_MAP = new HashMap<>();

  @Autowired
  private BankInfoMapper bankInfoMapper;

  public BankInfo getBankInfo(String accountId, Channel channel) throws BadRequestException {
    if (Objects.equals(channel, Channel.PAY)) return PAY_MAP.get(accountId);
    if (Objects.equals(channel, Channel.AGENCY)) return AGENCY_MAP.get(accountId);
    LOGGER.error("[getBankInfo]{}:{}", Exceptions.CHANNEL_INVALID.getErrMsg(), channel.name());
    throw new BadRequestException(Exceptions.CHANNEL_INVALID);
  }

  public void init() {
    LOGGER.info("bankinfo refresh start");
    PAY_MAP.clear();
    AGENCY_MAP.clear();

    List<BankInfo> bankInfos = bankInfoMapper.list();

    bankInfos.stream().filter(info -> Objects.equals(Channel.PAY, info.getChannel()))
        .forEach(info -> PAY_MAP.put(info.getAccountId(), info));

    bankInfos.stream().filter(info -> Objects.equals(Channel.AGENCY, info.getChannel()))
        .forEach(info -> AGENCY_MAP.put(info.getAccountId(), info));
    LOGGER.info("bankinfo refresh end");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
  }

}
