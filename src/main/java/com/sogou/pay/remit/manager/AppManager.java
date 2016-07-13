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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.sogou.pay.remit.entity.App;
import com.sogou.pay.remit.mapper.AppMapper;

import commons.utils.MapHelper;
import commons.utils.SignHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
@Component
public class AppManager implements InitializingBean {

  private static final boolean SIGN_SWITCH = false;

  private static final String SIGN_ITEM = "sign";

  private static final String APP_ITEM = "appId";

  public static final String SIGN_TYPE = "signType";

  private static final Set<String> EXCLUDES = ImmutableSet.of(SIGN_ITEM);

  private static Map<Integer, App> appMap = new HashMap<>();

  private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

  @Autowired
  private AppMapper appMapper;

  public String getKey(Integer appId) {
    App app = appMap.get(appId);
    return app == null ? null : app.getSignKey();
  }

  public String sign(Map<String, ?> map) {
    return sign(map, Integer.parseInt(String.valueOf(map.get(APP_ITEM))));
  }

  public String sign(Map<String, ?> map, int appId) {
    return SignHelper.sign(MapHelper.filter(map, EXCLUDES), getKey(appId));
  }

  public boolean checkSign(Map<String, ?> map) {
    return SIGN_SWITCH ? Objects.equals(map.get(SIGN_ITEM), sign(map)) : true;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
  }

  private void init() {
    LOGGER.info("app refresh start");
    List<App> apps = appMapper.list();
    appMap.clear();
    apps.forEach(app -> appMap.put(app.getId(), app));
    LOGGER.info("app refresh end");
  }

}
