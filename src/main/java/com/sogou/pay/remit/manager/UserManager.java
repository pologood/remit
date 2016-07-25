/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.mapper.UserMapper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Component
public class UserManager {

  @Autowired
  private UserMapper userMapper;

  public User getUserByUno(Integer uno) {
    return userMapper.getUserByUno(uno);
  }

  public User getUserById(Integer id) {
    return userMapper.getUserById(id);
  }

}
