/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import com.sogou.pay.remit.entity.User;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Repository
public interface UserMapper {

  class Sql {

    private final static String TABLE = "`user`";

    public static String getUserByUno() {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("uno = #{uno}").toString();
    }

    public static String getUserById() {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("id = #{id}").toString();
    }

  }

  @SelectProvider(type = Sql.class, method = "getUserByUno")
  User getUserByUno(@Param("uno") Integer uno);

  @SelectProvider(type = Sql.class, method = "getUserById")
  User getUserById(@Param("id") Integer id);

}
