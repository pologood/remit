/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;
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
      return sql.SET("status = #{status}").WHERE("appId = #{appId}").WHERE("batchNo = #{batchNo}")
          .WHERE("transferId = #{transferId}").toString();
    }

    public static String add(Map<String, Object> map) {
      List<?> details = (List<?>) map.get("details");
      StringBuilder sb = new StringBuilder("insert into ").append(TABLE)
          .append(" (appId, batchNo, transferId, inAccountName, inAccountId, amount, bankName, bankCity, memo) values ");
      for (int i = 0; i < details.size(); i++)
        sb.append("(").append(String.format("#{details[%d].appId}", i)).append(", ")
            .append(String.format("#{details[%d].batchNo}", i)).append(", ")
            .append(String.format("#{details[%d].transferId}", i)).append(", ")
            .append(String.format("#{details[%d].inAccountName}", i)).append(", ")
            .append(String.format("#{details[%d].inAccountId}", i)).append(", ")
            .append(String.format("#{details[%d].amount}", i)).append(", ")
            .append(String.format("#{details[%d].bankName}", i)).append(", ")
            .append(String.format("#{details[%d].bankCity}", i)).append(", ") //
            .append(String.format("#{details[%d].memo}", i)).append("),");
      return sb.substring(0, sb.length() - 1);
    }
  }

  @SelectProvider(type = Sql.class, method = "selectByBatchNo")
  List<TransferDetail> selectByBatchNo(@Param("appId") Integer appId, @Param("batchNo") String batchNo);

  @UpdateProvider(type = Sql.class, method = "update")
  int update(TransferDetail detail);

  @InsertProvider(type = Sql.class, method = "add")
  int add(@Param("details") List<TransferDetail> details);

}
