/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.manager.TransferDetailManager;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月19日;
//-------------------------------------------------------

@Api(name = "transfer detail API", description = "Read/Write/Update/ the transfer detail")
@RestController
@RequestMapping("/api")
public class TransferDetailController {

  @Autowired
  private TransferDetailManager transferDetailManager;

  @ApiMethod(description = "get transfer detail")
  @RequestMapping(value = "/transferDetail", method = RequestMethod.GET)
  public ApiResult<?> get(HttpServletRequest request, @RequestParam(name = "appId") @NotNull Integer appId,
      @RequestParam(name = "batchNo") @NotBlank String batchNo) {
    List<TransferDetail> details = transferDetailManager.selectByBatchNo(appId, batchNo);
    return CollectionUtils.isEmpty(details) ? ApiResult.notFound() : new ApiResult<>(details);
  }

}
