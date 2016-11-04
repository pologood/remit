/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sogou.pay.remit.api.SmsController.Status;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年9月2日;
//-------------------------------------------------------
@Service
public class SmsManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(SmsManager.class);

  public static final Map<String, Tuple2<String, Long>> CODE_MAP = new ConcurrentHashMap<>();

  private static long INTERVAL = TimeUnit.MINUTES.toMillis(5);

  private static final Random RANDOM = new Random();

  @Resource(name = "restTemplate")
  private RestTemplate restTemplate;

  public static Status validate(String mobile, String code) {
    LOGGER.info("{} validate code {}", mobile, code);
    Tuple2<String, Long> tuple = CODE_MAP.get(mobile);
    if (Objects.isNull(tuple) || !Objects.equals(code, tuple.f)) return Status.failed;
    if (Math.abs(System.currentTimeMillis() - tuple.s) > INTERVAL) return Status.expired;
    CODE_MAP.remove(mobile);
    return Status.success;
  }

  public ApiResult<?> send(String mobile) {
    String code = String.format(CODE_PATTERN, RANDOM.nextInt(LEN));
    CODE_MAP.put(mobile, new Tuple2<>(code, System.currentTimeMillis()));
    LOGGER.info("mobile is {} vcode is {}", mobile, code);
    return send(mobile, code);
  }

  private ApiResult<?> send(String mobile, String code) {
    @SuppressWarnings("unchecked")
    Map<String, Object> map = restTemplate.getForObject(String.format(SMS_URL, mobile, getContext(code)), Map.class);
    return Objects.equals(0, MapUtils.getInteger(map, "code")) ? ApiResult.ok()
        : ApiResult.internalError(String.valueOf(map));
  }

  private String getContext(String code) {
    return String.format(TEXT_PATTERN, code);
  }

  private static final int CODE_LEN = 6, LEN = (int) Math.pow(10, CODE_LEN);

  private static final String CODE_PATTERN = String.format("%%0%sd", CODE_LEN),
      SMS_URL = "http://sms.sogou/portal/mobile/smsproxy.php?number=%s&desc=%s&appid=sogoupassport&type=json",
      TEXT_PATTERN = "银企审核验证码%s,5分钟内有效";
}
