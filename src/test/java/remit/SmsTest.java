/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package remit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sogou.pay.remit.api.SmsController.Status;
import com.sogou.pay.remit.config.DaoConfig;
import com.sogou.pay.remit.config.RootConfig;
import com.sogou.pay.remit.manager.SmsManager;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年9月9日;
//-------------------------------------------------------
@ContextConfiguration(classes = { RootConfig.class, DaoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class SmsTest {

  @Autowired
  private SmsManager smsManager;

  @Test
  public void test() {
    String mobile = "18310165277";
    smsManager.send(mobile);
    Assert.assertEquals(Status.success, SmsManager.validate(mobile, SmsManager.CODE_MAP.get(mobile).f));;
  }

}
