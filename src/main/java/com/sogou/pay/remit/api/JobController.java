/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Objects;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.job.TransferJob;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月25日;
//-------------------------------------------------------
@Api(name = "job API", description = "Read/Write/Update/ the job ")
@RestController
@RequestMapping("/api")
public class JobController {

  @Autowired
  private TransferJob transferJob;

  @ApiMethod(description = "run job")
  @RequestMapping(value = "/job/{jobName}", method = RequestMethod.GET)
  public ApiResult<?> run(@PathVariable JobName jobName) {
    if (Objects.equals(JobName.PAY, jobName)) transferJob.pay();
    else if (Objects.equals(JobName.QUERY, jobName)) transferJob.query();
    return ApiResult.ok();
  }

  public enum JobName {
    PAY, QUERY;
  }

}
