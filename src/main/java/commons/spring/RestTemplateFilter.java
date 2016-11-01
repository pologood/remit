package commons.spring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

class ClientHttpResponseInvocationHandler implements InvocationHandler {

  final ClientHttpResponse proxied;

  ByteArrayInputStream inputStream;

  public ClientHttpResponseInvocationHandler(ClientHttpResponse response, byte[] data) {
    this.proxied = response;
    this.inputStream = new ByteArrayInputStream(data);
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName() == "getBody") {
      return inputStream;
    }
    return method.invoke(proxied, args);
  }
}

public class RestTemplateFilter implements ClientHttpRequestInterceptor {

  static final Logger logger = LoggerFactory.getLogger(RestTemplateFilter.class);

  private Charset charset;

  public RestTemplateFilter() {
    this(StandardCharsets.UTF_8);
  }

  public RestTemplateFilter(Charset charset) {
    this.charset = charset;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {

    if (!logger.isDebugEnabled()) return execution.execute(request, body);

    ClientHttpResponse response = execution.execute(request, body);
    byte[] bytes = StreamUtils.copyToByteArray(response.getBody());

    ClientHttpResponse proxy = (ClientHttpResponse) Proxy.newProxyInstance(RestTemplateFilter.class.getClassLoader(),
        new Class[] { ClientHttpResponse.class }, new ClientHttpResponseInvocationHandler(response, bytes));

    String input = (body == null || body.length == 0) ? "-" : new String(body, charset);
    String output = (bytes == null || bytes.length == 0) ? "-" : new String(bytes, charset);

    logger.debug("{} {} {} {} {}", request.getMethod(), request.getURI(), input, response.getRawStatusCode(), output);
    return proxy;
  }
}
