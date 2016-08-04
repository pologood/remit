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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.UserMapper;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Service
public class UserManager implements InitializingBean {

  @Autowired
  private UserMapper userMapper;

  private static final Map<Integer, User> UNO_MAP = new HashMap<>();

  private static final Map<Integer, User> ID_MAP = new HashMap<>();

  public static User getUserByUno(Integer uno) {
    return Objects.isNull(uno) ? null : UNO_MAP.get(uno);
  }

  public static User getUserById(Integer id) {
    return Objects.isNull(id) ? null : ID_MAP.get(id);
  }

  public void init() {
    ApiResult<?> result = list();
    if (ApiResult.isNotOK(result)) return;
    for (Object o : (List<?>) result.getData()) {
      User user = (User) o;
      UNO_MAP.put(user.getUno(), user);
      ID_MAP.put(user.getId(), user);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
  }

  public ApiResult<?> add(User user) {
    return userMapper.add(user) > 0 ? ApiResult.ok() : ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED);
  }

  public ApiResult<?> delete(Integer uno) {
    return userMapper.delete(uno) > 0 ? ApiResult.ok() : ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED);
  }

  public ApiResult<?> list() {
    List<User> users = userMapper.list();
    return CollectionUtils.isEmpty(users) ? ApiResult.notFound() : new ApiResult<>(users);
  }

  public ApiResult<?> update(Integer uno, String mobile, Role role) {
    return (Objects.isNull(mobile) && Objects.isNull(role)) || userMapper.update(uno, mobile, role) > 0 ? ApiResult.ok()
        : ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED);
  }

}
