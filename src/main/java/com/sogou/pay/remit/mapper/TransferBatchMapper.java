/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import com.sogou.pay.remit.entity.TransferBatch;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@Repository
public interface TransferBatchMapper {

  class Sql {

    private final static String TABLE = "`transfer_batch`";

    public static String selectByBatchNo(Map<String, Object> param) {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("appId = #{appId}").WHERE("batchNo = #{batchNo}").toString();
    }

    public static String list(Map<String, Object> map) {
      return new SQL().SELECT("*").FROM(TABLE).WHERE("status = #{status}").toString();
    }

    public static String update(TransferBatch batch) {
      SQL sql = new SQL().UPDATE(TABLE);
      if (StringUtils.isNotBlank(batch.getOutErrMsg())) sql.SET("outErrMsg = #{outErrMsg}");
      if (StringUtils.isNotBlank(batch.getAuditTimes())) sql.SET("auditTimes = #{auditTimes}");
      if (StringUtils.isNotBlank(batch.getAuditOpinions())) sql.SET("auditTimes = #{auditOpinions}");
      return sql.SET("status = #{status}").WHERE("appId = #{appId}").WHERE("batchNo = #{batchNo}").toString();
    }

    public static String add(TransferBatch batch) {
      SQL sql = new SQL().INSERT_INTO(TABLE)//
          .VALUES("version", "#{version}")//
          .VALUES("channel", "#{channel}")//
          .VALUES("appId", "#{appId}")//
          .VALUES("batchNo", "#{batchNo}")//
          .VALUES("transferAmount", "#{transferAmount}")//
          .VALUES("memo", "#{memo}")//
          .VALUES("outAccountId", "#{outAccountId}")//
          .VALUES("outAccountName", "#{outAccountName}")//
          .VALUES("branchCode", "#{branchCode}")//
          .VALUES("loginName", "#{loginName}")//
          .VALUES("busiMode", "#{busiMode}")//
          .VALUES("busiCode", "#{busiCode}")//
          .VALUES("transType", "#{transType}")//
          .VALUES("currency", "#{currency}")//
          .VALUES("createTime", "now()");//
      if (Objects.nonNull(batch.getSettleChannel())) sql.VALUES("settleChannel", "#{settleChannel}");
      if (StringUtils.isNotBlank(batch.getReserve())) sql.VALUES("reserve", "#{reserve}");
      if (Objects.nonNull(batch.getTransferCount())) sql.VALUES("transferCount", "#{transferCount}");
      return sql.toString();
    }
  }

  @SelectProvider(type = Sql.class, method = "selectByBatchNo")
  TransferBatch selectByBatchNo(@Param("appId") Integer appId, @Param("batchNo") String batchNo);

  @SelectProvider(type = Sql.class, method = "list")
  List<TransferBatch> list(int status);

  @UpdateProvider(type = Sql.class, method = "update")
  int update(TransferBatch batch);

  @InsertProvider(type = Sql.class, method = "add")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int add(TransferBatch batch);

}
