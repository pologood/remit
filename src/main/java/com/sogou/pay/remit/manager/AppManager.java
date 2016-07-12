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

  private static final String SIGN_ITEM = "sign";

  private static final String APP_ITEM = "appId";

  private static final Set<String> EXCLUDES = ImmutableSet.of(SIGN_ITEM);

  private static Map<Integer, App> appMap = new HashMap<>();

  private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

  @Autowired
  private AppMapper appMapper;

  public String getKey(Integer appId) {
    App app = appMap.get(appId);
    return app == null ? null : app.getKey();
  }

  public String sign(Map<String, String> map) {
    return sign(map, Integer.parseInt(map.get(APP_ITEM)));
  }

  public String sign(Map<String, String> map, int appId) {
    String sign = SignHelper.sign(MapHelper.filter(map, EXCLUDES), getKey(appId));
    map.put(SIGN_ITEM, sign);
    return sign;
  }

  public boolean checkSign(Map<String, String> map) {
    return Objects.equals(map.get(SIGN_ITEM), sign(map));
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
