package commons.spring;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.core.env.Environment;
import commons.utils.XssHelper;

public class XssFilter implements Filter {

  private Set<String> ignoreKey = new HashSet<>();

  public XssFilter(Environment env) {
    String s = env.getProperty("xssfilter.ignorekey");
    if (s != null) {
      ignoreKey.addAll(Arrays.asList(s.split(",")));
    }
  }

  public void init(FilterConfig arg) throws ServletException {
    // nothing to do
  }

  public void destroy() {
    // nothing to do
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String method = req.getMethod();

    ServletRequest reqWrap = request;
    if (method.equals("POST") || method.equals("PUT")) {
      reqWrap = new HttpServletRequestWrapImpl(req, ignoreKey);
    }

    chain.doFilter(reqWrap, response);
  }

  private static class HttpServletRequestWrapImpl extends HttpServletRequestWrapper {

    private Set<String> ignoreKey;

    public HttpServletRequestWrapImpl(HttpServletRequest request, Set<String> ignoreKey) {
      super(request);
      this.ignoreKey = ignoreKey;
    }

    @Override
    public String getParameter(String name) {
      String value = super.getParameter(name);
      if (ignoreKey.contains(name)) {
        return value;
      } else {
        return XssHelper.escape(value);
      }
    }

    @Override
    public String[] getParameterValues(String name) {
      String values[] = super.getParameterValues(name);
      if (values == null || ignoreKey.contains(name)) {
        return values;
      } else {
        String newValues[] = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
          newValues[i] = XssHelper.escape(values[i]);
        }
        return newValues;
      }
    }
  }
}
