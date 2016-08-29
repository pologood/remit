/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.spring;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StreamUtils;

import com.sogou.pay.remit.api.SignInterceptor;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年8月2日;
//-------------------------------------------------------
public class MultiReadableFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(SignInterceptor.FILTERED_METHOD.contains(((HttpServletRequest) request).getMethod())
        ? new MultiReadableRequest((HttpServletRequest) request) : request, response);
  }

  @Override
  public void destroy() {
  }

  private static class MultiReadableRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> paramMap;

    private byte rawData[];

    public MultiReadableRequest(HttpServletRequest request) throws IOException {
      super(request);
      paramMap = request.getParameterMap();
      rawData = StreamUtils.copyToByteArray(super.getInputStream());
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return paramMap;
    }

    @Override
    public String getParameter(String name) {
      String[] values = paramMap.get(name);
      return Objects.isNull(values) || values.length == 0 ? null : values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
      return paramMap.get(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      ServletInputStreamImpl stream = new ServletInputStreamImpl();
      stream.setData(rawData);
      return stream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
      return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private static class ServletInputStreamImpl extends ServletInputStream {

      public ByteArrayInputStream stream;

      public void setData(byte[] data) {
        stream = new ByteArrayInputStream(data);
      }

      @Override
      public int read() throws IOException {
        return stream.read();
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public boolean isFinished() {
        return stream.available() > 0;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
      }
    }
  }
}
