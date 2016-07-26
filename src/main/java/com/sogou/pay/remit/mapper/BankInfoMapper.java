/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import com.sogou.pay.remit.entity.BankInfo;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
@Repository
public interface BankInfoMapper {

  class Sql {

    private final static String TABLE = "`bank_info`";

    public static String list() {
      return new SQL().SELECT("*").FROM(TABLE).toString();
    }

  }

  @SelectProvider(type = Sql.class, method = "list")
  List<BankInfo> list();

}
