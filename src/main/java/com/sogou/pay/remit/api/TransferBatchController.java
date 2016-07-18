/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.SignType;
import com.sogou.pay.remit.entity.TransferBatch.Status;
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
  private TransferBatchManager transferBatchManager;

  @ApiMethod(description = "add transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.POST)
  public ApiResult<?> add(HttpServletRequest request, @ApiQueryParam @RequestBody @Valid TransferBatch batch,
      BindingResult bindingResult) throws IOException {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:batch={}", batch);
      return ApiResult.bindingResult(bindingResult);
    }
    return transferBatchManager.add(batch);
  }

  @ApiMethod(description = "get transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.GET)
  public ApiResult<?> get(@RequestParam(name = "appId") @NotNull Integer appId,
      @RequestParam(name = "batchNo") @NotBlank String batchNo, @RequestParam(name = "sign") @NotBlank String sign,
      @RequestParam(name = "signType") @NotNull SignType signType) {
    return transferBatchManager.get(appId, batchNo, true);
  }

  @ApiMethod(description = "get transfer batch with status")
  @RequestMapping(value = "/transferBatch/{status}", method = RequestMethod.GET)
  public ApiResult<?> get(@PathVariable @NotNull Status status) {
    return transferBatchManager.list();
  }

  @ApiMethod(description = "update transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestParam(name = "appId") @NotNull int appId,
      @RequestParam(name = "batchNo") @NotBlank String batchNo, @RequestParam(name = "status") Status status,
      @RequestParam(name = "opinion", required = false) Optional<String> opinion) {
    //TODO uid
    return transferBatchManager.update(appId, batchNo, 1, status, Optional.empty(), opinion.orElse(null));
  }

}
