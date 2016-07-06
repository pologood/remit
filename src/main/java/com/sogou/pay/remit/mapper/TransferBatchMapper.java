/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import com.sogou.pay.remit.entity.TransferBatch;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
public interface TransferBatchMapper {

  class Sql {

    private final static String TABLE = "`transfer_batch`";

    public static String selectByBatchNo(Map<String, Object> param) {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("appId = #{appId}").WHERE("batchNo = #{batchNo}").toString();
    }
  }

  @SelectProvider(type = Sql.class, method = "selectByBatchNo")
  TransferBatch selectByBatchNo(@Param("appId") Integer appId, @Param("batchNo") String batchNo);

  List<TransferBatch> list(int status);

}
