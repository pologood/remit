/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
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
  public ApiResult<?> get(@ApiQueryParam(name = "appId", description = "业务线") @RequestParam Integer appId,
      @ApiQueryParam(name = "batchNo", description = "批次号") @RequestParam String batchNo) {
    List<TransferDetail> details = transferDetailManager.selectByBatchNo(appId, batchNo);
    return new ApiResult<>(details);
  }

}
