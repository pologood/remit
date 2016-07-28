/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.api;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.entity.User.Role;
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
  public ApiResult<?> run(HttpServletRequest request, @PathVariable JobName jobName) {
    if (!Objects.equals(Role.ADMIN, ((User) request.getAttribute("remituser")).getRole())) return ApiResult.forbidden();
    if (Objects.equals(JobName.pay, jobName)) transferJob.pay();
    else if (Objects.equals(JobName.query, jobName)) transferJob.query();
    else if (Objects.equals(JobName.callback, jobName)) transferJob.callback();
    return ApiResult.ok();
  }

  public enum JobName {
    pay, query, callback;
  }

}
