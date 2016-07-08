/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.manager.TransferBatchManager;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------

@Api(name = "transfer batch API", description = "Read/Write/Update/ the transfer batch")
@RestController
@RequestMapping("/api")
public class TransferBatchController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchController.class);

  @Autowired
  private TransferBatchManager manager;

  @ApiMethod(description = "add transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.POST)
  public ApiResult<?> add(@ApiQueryParam @Valid TransferBatch batch, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:batch={}", batch);
      return ApiResult.bindingResult(bindingResult);
    }
    return manager.add(batch);
  }

  @ApiMethod(description = "get transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.GET)
  public ApiResult<?> get(@RequestParam(name = "appId", required = false) Optional<Integer> appId,
      @RequestParam(name = "batchNo", required = false) Optional<String> batchNo,
      @RequestParam(name = "status", required = false) Optional<Integer> status) {
    if (appId.isPresent() && batchNo.isPresent()) return manager.get(appId.get(), batchNo.get());
    if (status.isPresent()) return manager.list(status.get());
    return ApiResult.badRequest("batchNo and appId are required or status is required");
  }

  @ApiMethod(description = "update transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestParam(name = "appId") @Min(value = 1) int appId,
      @RequestParam(name = "batchNo") @Size(min = 1) String batchNo,
      @RequestParam(name = "status") @Min(value = 1) int status,
      @RequestParam(name = "opinion", required = false) Optional<String> opinion) {
    return manager.update(appId, batchNo, status, null, opinion.orElse(null));
  }

}
