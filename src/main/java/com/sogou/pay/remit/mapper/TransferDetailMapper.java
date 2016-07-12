/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import com.sogou.pay.remit.entity.TransferDetail;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月8日;
//-------------------------------------------------------
@Repository
public interface TransferDetailMapper {

  class Sql {

    private final static String TABLE = "`transfer_detail`";

    public static String selectByBatchNo(Map<String, Object> param) {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("appId = #{appId}").WHERE("batchNo = #{batchNo}").toString();
    }

    public static String update(TransferDetail detail) {
      SQL sql = new SQL().UPDATE(TABLE);
      if (StringUtils.isNotBlank(detail.getOutErrMsg())) sql.SET("outErrMsg = #{outErrMsg}");
      return sql.SET("status = #{status}").WHERE("appId = #{appId}").WHERE("batchNo = #{batchNo}").toString();
    }

    public static String add(TransferDetail detail) {
      SQL sql = new SQL().INSERT_INTO(TABLE)//
          .VALUES("appId", "#{appId}")//
          .VALUES("batchNo", "#{batchNo}")//
          .VALUES("transferId", "#{transferId}")//
          .VALUES("inAcountName", "#{inAcountName}")//
          .VALUES("inAccountId", "#{inAccountId}")//
          .VALUES("amount", "#{amount}");
      if (StringUtils.isNoneBlank(detail.getBankCity(), detail.getBankName()))
        sql.VALUES("bankName", "#{bankName}").VALUES("bankCity", "#{bankCity}");
      if (StringUtils.isNotBlank(detail.getMemo())) sql.VALUES("memo", "#{memo}");
      return sql.toString();
    }
  }

  @SelectProvider(type = Sql.class, method = "selectByBatchNo")
  TransferDetail selectByBatchNo(@Param("appId") Integer appId, @Param("batchNo") String batchNo);

  @UpdateProvider(type = Sql.class, method = "update")
  int update(TransferDetail detail);

  //TODO batch insert
  @InsertProvider(type = Sql.class, method = "add")
  int add(TransferDetail detail);

}
