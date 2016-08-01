/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
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
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.User;
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

  @ApiMethod(consumes = "application/json", description = "add transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.POST, consumes = "application/json")
  public ApiResult<?> add(HttpServletRequest request,
      @ApiBodyObject(clazz = TransferBatch.class) @RequestBody @Valid TransferBatch batch, BindingResult bindingResult)
          throws Exception {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:batch={}", batch);
      return ApiResult.bindingResult(bindingResult);
    }
    return transferBatchManager.add(batch.makeXssSafe());
  }

  @ApiMethod(description = "get transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.GET)
  public ApiResult<?> get(
      @ApiQueryParam(name = "appId", description = "业务线") @RequestParam(name = "appId") @NotNull Integer appId,
      @ApiQueryParam(name = "batchNo", description = "批次号") @RequestParam(name = "batchNo") @NotBlank String batchNo) {
    return transferBatchManager.get(appId, batchNo, true);
  }

  @ApiMethod(description = "get transfer batch with status")
  @RequestMapping(value = "/transferBatch/{channel}", method = RequestMethod.GET)
  public ApiResult<?> get(HttpServletRequest request,
      @ApiPathParam(clazz = Channel.class, name = "channel", description = "渠道") @NotNull @PathVariable("channel") Channel channel) {
    User user = (User) request.getAttribute("remituser");
    return transferBatchManager.list(channel, Status.getToDoStatus(user.getRole()));
  }

  @ApiMethod(description = "reject transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}/{batchNo}", method = RequestMethod.PUT)
  public ApiResult<?> update(HttpServletRequest request,
      @ApiPathParam(name = "appId", description = "业务线") @PathVariable("appId") @NotNull Integer appId,
      @ApiPathParam(name = "batchNo", description = "批次号") @PathVariable("batchNo") @NotBlank String batchNo,
      @RequestParam(name = "opinion") String opinion) {
    User user = (User) request.getAttribute("remituser");
    Status status = Status.getRejectedStatus(user.getRole());
    return transferBatchManager.audit(appId, batchNo, user, status, opinion);
  }

  @ApiMethod(description = "approve transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}", method = RequestMethod.PUT)
  public ApiResult<?> update(HttpServletRequest request,
      @ApiPathParam(name = "appId", description = "业务线") @PathVariable("appId") @NotNull Integer appId,
      @ApiQueryParam(name = "list", description = "批次号列表") @RequestParam(name = "list") @NotEmpty List<String> batchNos) {
    User user = (User) request.getAttribute("remituser");
    Status status = Status.getApprovedStatus(user.getRole());
    if (Objects.isNull(status)) return ApiResult.forbidden();
    return transferBatchManager.batchUpdateTransferBatchStatus(user, appId, batchNos, status);
  }

}
