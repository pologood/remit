/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Repository
public interface UserMapper {

  class Sql {

    private final static String TABLE = "`user`";

    public final static String DELETE = "DELETE FROM " + TABLE + " WHERE uno = #{uno}";

    public static String list() {
      return new SQL().SELECT("*").FROM(TABLE).toString();
    }

    public static String update(Map<String, Object> map) {
      SQL sql = new SQL().UPDATE(TABLE);
      if (Objects.nonNull(map.get("mobile"))) sql.SET("mobile = #{mobile}");
      if (Objects.nonNull(map.get("role"))) sql.SET("mobile = #{role}");
      return sql.WHERE("uno = #{uno}").toString();
    }

    public static String add(User user) {
      return new SQL().INSERT_INTO(TABLE).VALUES("uno", "#{uno}").VALUES("name", "#{name}").VALUES("email", "#{email}")
          .VALUES("mobile", "#{mobile}").VALUES("role", "#{role}").toString();
    }

  }

  @SelectProvider(type = Sql.class, method = "list")
  List<User> list();

  @Delete(Sql.DELETE)
  int delete(@Param("uno") Integer uno);

  @InsertProvider(type = Sql.class, method = "add")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int add(User user);

  @UpdateProvider(type = Sql.class, method = "update")
  int update(@Param("uno") Integer uno, @Param("mobile") String mobile, @Param("role") Role role);

}
