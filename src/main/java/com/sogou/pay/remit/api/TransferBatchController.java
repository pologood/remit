/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

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
import org.springframework.format.annotation.DateTimeFormat;
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
import com.sogou.pay.remit.entity.User.Role;
import com.sogou.pay.remit.job.TransferJob;
import com.sogou.pay.remit.manager.PandoraManager;
import com.sogou.pay.remit.manager.TransferBatchManager;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.ErrorCode;

import commons.utils.Tuple2;

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

  @Autowired
  private PandoraManager pandoraManager;

  @Autowired
  private TransferJob job;

  @ApiMethod(consumes = "application/json", description = "add transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.POST, consumes = "application/json")
  public ApiResult<?> add(@ApiBodyObject @RequestBody @Valid TransferBatch batch, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      LOGGER.error("[add]bad request:batch={}", batch);
      return ApiResult.bindingResult(bindingResult);
    }
    return transferBatchManager.add(batch);
  }

  @ApiMethod(description = "get transfer batch")
  @RequestMapping(value = "/transferBatch", method = RequestMethod.GET)
  public ApiResult<?> get(@ApiQueryParam(name = "appId", description = "业务线") @RequestParam int appId,
      @ApiQueryParam(name = "batchNo", description = "批次号") @RequestParam String batchNo) {
    return transferBatchManager.get(appId, batchNo, true, false);
  }

  @ApiMethod(description = "get transfer batch with status")
  @RequestMapping(value = "/transferBatch/{channel}/{status}", method = RequestMethod.GET)
  public ApiResult<?> get(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "channel", description = "渠道") @PathVariable Channel channel,
      @ApiPathParam(name = "status", description = "审批状态") @PathVariable AuditStatus status,
      @ApiQueryParam(name = "beginTime", description = "起始时间", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Optional<LocalDateTime> beginTime,
      @ApiQueryParam(name = "endTime", description = "结束时间", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Optional<LocalDateTime> endTime) {
    Tuple2<Integer, Integer> tuple = STATUS_MAP.get(status).apply(user);
    ApiResult<?> result = transferBatchManager.list(channel, tuple.f,
        Objects.equals(AuditStatus.INIT, status) ? null : user, beginTime.orElse(null), endTime.orElse(null), tuple.s);
    return Objects.equals(ErrorCode.NOT_FOUND.getCode(), result.getCode()) ? ApiResult.ok() : result;
  }

  @ApiMethod(description = "reject transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}/{batchNo}", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "appId", description = "业务线") @PathVariable int appId,
      @ApiPathParam(name = "batchNo", description = "批次号") @PathVariable String batchNo,
      @ApiQueryParam(name = "opinion", description = "驳回意见", required = false) @RequestParam Optional<String> opinion) {
    return transferBatchManager.audit(appId, batchNo, user, Status.getRejectedStatus(user.getRole()),
        opinion.orElse(null));
  }

  @ApiMethod(description = "approve transfer batch")
  @RequestMapping(value = "/transferBatch/{appId}", method = RequestMethod.PUT)
  public ApiResult<?> update(@RequestAttribute(name = UserController.USER_ATTRIBUTE) User user,
      @ApiPathParam(name = "appId", description = "业务线") @PathVariable Integer appId,
      @ApiQueryParam(name = "batchNos", description = "批次号列表") @RequestParam(name = "batchNos[]") List<String> batchNos,
      @ApiQueryParam(name = "amount", description = "审批通过总金额") @RequestParam(name = "amount") BigDecimal amount) {
    LOGGER.info("user {} approves amount {} batchNos {}", user, amount, batchNos);
    String message;
    if (Objects.nonNull(message = pandoraManager.push(
        String.format("%s于%s审核通过%d单 金额%s", user.getName(), LocalDateTime.now().toString(), batchNos.size(), amount))))
      LOGGER.error("push message error:{}", message);;
    return transferBatchManager.batchUpdateTransferBatchStatus(user, appId, batchNos,
        Status.getApprovedStatus(user.getRole()));
  }

  @ApiObject(name = "AuditStatus", description = "审批状态")
  public enum AuditStatus {
    INIT, REJECTED, APPROVED;
  }

  private static final Map<Status, Integer> APPROVAL_MAP = ImmutableMap.of(Status.JUNIOR_APPROVED,
      Integer.parseInt("111111111100", 2), Status.SENIOR_APPROVED, Integer.parseInt("111111110000", 2),
      Status.FINAL_APPROVED, Integer.parseInt("111111000000", 2));

  private static final Map<AuditStatus, Function<User, Tuple2<Integer, Integer>>> STATUS_MAP = ImmutableMap.of(
      AuditStatus.INIT,
      user -> new Tuple2<>(Status.getToDoStatus(user.getRole()).getValue(), Role.getLimit(user.getRole())),
      AuditStatus.REJECTED, user -> new Tuple2<>(Status.getRejectedStatus(user.getRole()).getValue(), null),
      AuditStatus.APPROVED, user -> new Tuple2<>(APPROVAL_MAP.get(Status.getApprovedStatus(user.getRole())), null));

  @RequestMapping(value = "/job/pay", method = RequestMethod.GET)
  public ApiResult<?> pay() {
    job.pay();
    return ApiResult.ok();

  }

  @RequestMapping(value = "/job/query", method = RequestMethod.GET)
  public ApiResult<?> query() {
    job.query();
    return ApiResult.ok();
  }

  @RequestMapping(value = "/job/callback", method = RequestMethod.GET)
  public ApiResult<?> callback() {
    job.callback();
    return ApiResult.ok();
  }

  @RequestMapping(value = "/job/email", method = RequestMethod.GET)
  public ApiResult<?> email() {
    job.email();
    return ApiResult.ok();
  }

  @RequestMapping(value = "/transferBatch/daily", method = RequestMethod.GET)
  public ApiResult<?> getDailyReport(
      @ApiQueryParam(name = "date", description = "日报日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam Optional<LocalDate> date) {
    LocalDate day = date.orElse(LocalDate.now().minusDays(1));
    ApiResult<List<TransferBatch>> result = transferBatchManager.list(null,
        (Status.SUCCESS.getValue() | Status.PART.getValue()), null, day.atStartOfDay(), day.plusDays(1).atStartOfDay(),
        null, false);
    if (ApiResult.isNotOK(result)) return result;
    List<TransferBatch> batchs = result.getData();
    int successCount = 0;
    BigDecimal successAmount = BigDecimal.ZERO;
    for (TransferBatch batch : batchs)
      if (Objects.equals(Channel.PAY, batch.getChannel())) {
        successCount++;
        successAmount = successAmount.add(batch.getTransferAmount());
      } else {
        successCount += batch.getSuccessCount();
        successAmount = successAmount.add(batch.getSuccessAmount());
      }
    return new ApiResult<>(new Tuple2<>(successCount, successAmount));
  }
}
