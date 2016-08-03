/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.manager.TransferBatchManager;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.ErrorCode;

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
  public ApiResult<?> add(@ApiBodyObject(clazz = TransferBatch.class) @RequestBody @Valid TransferBatch batch,
      BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:batch={}", batch);
      return ApiResult.bindingResult(bindingResult);
    }
    return transferBatchManager.add(batch.makeXssSafe());
  }

  @ApiMethod(description = "get transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.GET)
  public ApiResult<?> get(
      @ApiQueryParam(name = "appId", description = "业务线") @RequestParam(name = "appId") Integer appId,
      @ApiQueryParam(name = "batchNo", description = "批次号") @RequestParam(name = "batchNo") String batchNo) {
    return transferBatchManager.get(appId, batchNo, true);
  }

  @ApiMethod(description = "get transfer batch with status")
  @RequestMapping(value = "/transferBatch/{channel}/{status}", method = RequestMethod.GET)
  public ApiResult<?> get(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(clazz = Channel.class, name = "channel", description = "渠道") @PathVariable("channel") Channel channel,
      @ApiPathParam(clazz = AuditStatus.class, name = "status", description = "审批状态") @PathVariable("status") AuditStatus status) {
    ApiResult<?> result = transferBatchManager.list(channel, STATUS_MAP.get(status).getStatus(user, status),
        Objects.equals(AuditStatus.INIT, status) ? null : user);
    return Objects.equals(ErrorCode.NOT_FOUND.getCode(), result.getCode()) ? ApiResult.ok() : result;
  }

  @ApiMethod(description = "reject transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}/{batchNo}", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "appId", description = "业务线") @PathVariable("appId") int appId,
      @ApiPathParam(name = "batchNo", description = "批次号") @PathVariable("batchNo") String batchNo,
      @ApiQueryParam(name = "opinion", description = "驳回意见") @RequestParam(name = "opinion") Optional<String> opinion) {
    Status status = Status.getRejectedStatus(user.getRole());
    if (Objects.isNull(status)) return ApiResult.unAuthorized();
    return transferBatchManager.audit(appId, batchNo, user, status, opinion.orElse(null));
  }

  @ApiMethod(description = "approve transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "appId", description = "业务线") @PathVariable("appId") Integer appId,
      @ApiQueryParam(name = "batchNos", description = "批次号列表") @RequestParam(name = "batchNos[]") List<String> batchNos) {
    Status status = Status.getApprovedStatus(user.getRole());
    if (Objects.isNull(status)) return ApiResult.unAuthorized();
    return transferBatchManager.batchUpdateTransferBatchStatus(user, appId, batchNos, status);
  }

  @ApiObject(name = "AuditStatus", description = "审批状态")
  public enum AuditStatus {
    INIT, REJECTED, APPROVED;
  }

  private static final Map<AuditStatus, StatusGetter> STATUS_MAP = ImmutableMap.of(AuditStatus.INIT,
      (user, auditStatus) -> Status.getToDoStatus(user.getRole()), AuditStatus.REJECTED,
      (user, auditStatus) -> Status.getRejectedStatus(user.getRole()), AuditStatus.APPROVED,
      (user, auditStatus) -> Status.getApprovedStatus(user.getRole()));

  @FunctionalInterface
  public interface StatusGetter {

    public Status getStatus(User user, AuditStatus auditStatus);
  }

}
