package org.example.taskmanager03.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory baseFactory = new SimpleClientHttpRequestFactory();
        baseFactory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        baseFactory.setReadTimeout((int) Duration.ofSeconds(3).toMillis());

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(baseFactory));

        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter jackson) {
                List<MediaType> types = new ArrayList<>(jackson.getSupportedMediaTypes());
                if (!types.contains(MediaType.TEXT_HTML)) {
                    types.add(MediaType.TEXT_HTML);
                    jackson.setSupportedMediaTypes(types);
                }
            }
        }

        restTemplate.getInterceptors().add(new LoggingInterceptor());

        return restTemplate;
    }

    static class LoggingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(org.springframework.http.HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            long start = System.nanoTime();
            log.debug("HTTP {} {}", request.getMethod(), request.getURI());
            try {
                ClientHttpResponse response = execution.execute(request, body);
                long tookMs = (System.nanoTime() - start) / 1_000_000;
                try {
                    String snippet = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                    int status = response.getStatusCode().value();
                    log.debug("<- {} {} ({} ms) [{}]", status, request.getURI(), tookMs, truncate(snippet, 200));
                } catch (Exception ignore) {
                    try {
                        int status = response.getStatusCode().value();
                        log.debug("<- {} {} ({} ms)", status, request.getURI(), tookMs);
                    } catch (Exception e) {
                        log.debug("<- (unknown status) {} ({} ms)", request.getURI(), tookMs);
                    }
                }
                return response;
            } catch (RestClientResponseException rre) {
                long tookMs = (System.nanoTime() - start) / 1_000_000;
                int status = rre.getStatusCode().value();
                String bodyText = safeBody(rre);
                log.warn("HTTP error {} {} -> status={} ({} ms) body=[{}]", request.getMethod(), request.getURI(), status, tookMs, truncate(bodyText, 500));
                throw rre;
            } catch (RuntimeException ex) {
                long tookMs = (System.nanoTime() - start) / 1_000_000;
                log.warn("HTTP failure {} {} ({} ms): {}", request.getMethod(), request.getURI(), tookMs, ex.toString());
                throw ex;
            }
        }

        private String safeBody(RestClientResponseException ex) {
            try {
                return ex.getResponseBodyAsString(StandardCharsets.UTF_8);
            } catch (Exception e) {
                return "<no body>";
            }
        }

        private String truncate(String s, int max) {
            if (s == null) return null;
            return s.length() <= max ? s : s.substring(0, max) + "...";
        }
    }
}
