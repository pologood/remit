/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package remit;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sogou.pay.remit.config.DaoConfig;
import com.sogou.pay.remit.config.RootConfig;
import com.sogou.pay.remit.job.TransferJob;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年8月26日;
//-------------------------------------------------------
@ContextConfiguration(classes = { RootConfig.class, DaoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class JobTest {

  @Autowired
  private TransferJob transferJob;

  @Test
  public void emailTest() {
    transferJob.email();
  }

  @Test
  public void queryTest() {
    transferJob.query();
  }

  @Test
  public void detailTest() {
    transferJob.detail(LocalDate.now().minusDays(1));
  }

}
