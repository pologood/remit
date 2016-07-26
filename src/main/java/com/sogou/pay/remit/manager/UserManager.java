/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.mapper.UserMapper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Component
public class UserManager implements InitializingBean {

  @Autowired
  private UserMapper userMapper;

  private static List<User> users = new ArrayList<>();

  private static final Map<Integer, User> UNO_MAP = new HashMap<>();

  private static final Map<Integer, User> ID_MAP = new HashMap<>();

  public static User getUserByUno(Integer uno) {
    return UNO_MAP.get(uno);
  }

  public static User getUserById(Integer id) {
    return ID_MAP.get(id);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    users = userMapper.list();
    users.forEach(user -> {
      UNO_MAP.put(user.getUno(), user);
      ID_MAP.put(user.getId(), user);
    });
  }

}
