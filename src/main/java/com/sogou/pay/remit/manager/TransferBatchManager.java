/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.mapper.TransferBatchMapper;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@Component
public class TransferBatchManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchManager.class);

  @Autowired
  private TransferBatchMapper mapper;

  public ApiResult<?> get(int appId, String batchNo) {
    TransferBatch batch = mapper.selectByBatchNo(appId, batchNo);
    if (Objects.isNull(batch)) {
      LOGGER.error("[get]not found:appId={},batchNo={}", appId, batchNo);
      return ApiResult.notFound();
    }
    return new ApiResult<>(batch);
  }

  public ApiResult<?> list(int status) {
    List<TransferBatch> batchs = mapper.list(status);
    return CollectionUtils.isEmpty(batchs) ? ApiResult.notFound() : new ApiResult<>(batchs);
  }

  public ApiResult<?> add(TransferBatch batch) {
    // TODO Auto-generated method stub
    return null;

  }

  public ApiResult<?> update(int appId, String batchNo, int status) {
    ApiResult<?> result = get(appId, batchNo);
    if (ApiResult.isNotOK(result)) return result;
    return null;

  }

}
