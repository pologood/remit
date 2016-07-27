/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
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
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.manager.TransferBatchManager;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------

@Api(name = "transfer batch API", description = "Read/Write/Update/ the transfer batch")
@RestController
@RequestMapping("/api")
public class TransferBatchController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchController.class);

  private static final char SPLIT_CHAR = '^';

  @Autowired
  private TransferBatchManager transferBatchManager;

  @ApiMethod(description = "add transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.POST)
  public ApiResult<?> add(HttpServletRequest request, @ApiQueryParam @RequestBody @Valid TransferBatch batch,
      BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:batch={}", batch);
      return ApiResult.bindingResult(bindingResult);
    }
    return transferBatchManager.add(batch.makeXssSafe());
  }

  @ApiMethod(description = "get transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.GET)
  public ApiResult<?> get(@RequestParam(name = "appId") @NotNull Integer appId,
      @RequestParam(name = "batchNo") @NotBlank String batchNo) {
    return transferBatchManager.get(appId, batchNo, true);
  }

  @ApiMethod(description = "get transfer batch with status")
  @RequestMapping(value = "/transferBatch/{status}", method = RequestMethod.GET)
  public ApiResult<?> get(@PathVariable @NotNull Status status) {
    return transferBatchManager.list(status);
  }

  @ApiMethod(description = "reject transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}/{batchNo}", method = RequestMethod.PUT)
  public ApiResult<?> update(HttpServletRequest request, @PathVariable("appId") @NotNull Integer appId,
      @PathVariable("batchNo") @NotBlank String batchNo,
      @RequestParam(name = "opinion", required = false) Optional<String> opinion) {
    User user = (User) request.getAttribute("remituser");
    Status status = Status.getRejectedStatus(user.getRole());
    return transferBatchManager.audit(appId, batchNo, user, status, opinion.orElse(null));
  }

  @ApiMethod(description = "approve transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.PUT)
  public ApiResult<?> update(HttpServletRequest request, @RequestParam(name = "list") @NotEmpty List<String> batchNos) {
    User user = (User) request.getAttribute("remituser");
    List<Tuple2<Integer, String>> list = new ArrayList<>();
    for (String s : batchNos) {
      String[] a = StringUtils.split(s, SPLIT_CHAR);
      list.add(new Tuple2<>(Integer.parseInt(a[0]), a[1]));
    }
    return transferBatchManager.batchUpdateTransferBatchStatus(user, list, Status.getApprovedStatus(user.getRole()));
  }

}
