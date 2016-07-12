/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import com.sogou.pay.remit.entity.App;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
public interface AppMapper {

  class Sql {

    private final static String TABLE = "`app`";

    public static String list() {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("status = 1").toString();
    }
  }

  @SelectProvider(type = Sql.class, method = "list")
  List<App> list();

}
