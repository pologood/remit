/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月14日;
//-------------------------------------------------------
public class Httpclient {

  private static final HttpClient client = HttpClientBuilder.create().build();

  public static void post(String url, String context) {
    //TODO
  }

}
