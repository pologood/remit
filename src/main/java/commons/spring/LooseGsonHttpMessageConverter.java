package commons.spring;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class LooseGsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

  public LooseGsonHttpMessageConverter() {
    this(DEFAULT_CHARSET);
  }

  public LooseGsonHttpMessageConverter(Charset charset) {
    List<MediaType> types = Arrays.asList(new MediaType("text", "plain", charset),
        new MediaType("application", "json", charset), new MediaType("application", "*+json", charset),
        new MediaType("application", "octet-stream", charset));
    super.setSupportedMediaTypes(types);
  }
}
