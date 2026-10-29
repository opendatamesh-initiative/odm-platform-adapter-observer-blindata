package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/*
https://springframework.guru/using-resttemplate-with-apaches-httpclient/
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        // set a total amount of connections across all HTTP routes
        poolingConnectionManager.setMaxTotal(5);
        // set a maximum amount of connections for each HTTP route in pool
        poolingConnectionManager.setDefaultMaxPerRoute(2);
        poolingConnectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(60)) // time to open a server connection
                .setSocketTimeout(Timeout.ofSeconds(60)) //  time between data packets
                .build());
        return poolingConnectionManager;
    }

    /*
     * A connection Keep-Alive strategy determines how long a connection may remain unused in the pool until it is closed.
     * This ensures that connections that are no longer needed are closed again promptly.
     * The bean implements the following behavior: If the server does not send a Keep-Alive header in the response,
     * the connections are kept alive for 20 seconds by default.
     * This implementation is a workaround to bypass the Apache Keep-Alive strategy.
     * Apaches strategy assumes that connections should remain alive indefinitely if the server does not send
     * a Keep-Alive header.
     * This standard behavior is now explicitly circumvented by our implementation.
     */
    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (httpResponse, httpContext) -> {
            Iterator<HeaderElement> elementIterator = MessageSupport.iterate(httpResponse, HeaderElements.KEEP_ALIVE);

            while (elementIterator.hasNext()) {
                HeaderElement element = elementIterator.next();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return TimeValue.ofSeconds(Long.parseLong(value));
                }
            }

            return TimeValue.ofSeconds(20);
        };
    }

    /* Furthermore, we want to configure a connection monitor that runs every 20 seconds
     * and closes outdated connections as well as long waiting connections.
     */
    @Bean
    public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool) {
        return new Runnable() {
            final Logger logger = LoggerFactory.getLogger(getClass());

            @Override
            @Scheduled(fixedDelay = 60000)
            public void run() {
                // only if connection pool is initialised
                if (pool != null) {
                    logger.debug("Cleaning expired and idle connections");
                    pool.closeExpired();
                    pool.closeIdle(TimeValue.of(20, TimeUnit.SECONDS));
                }
            }
        };
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(60)) // time to request connection from the pool
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplateBuilder()
                .requestFactory(() -> requestFactory);
    }
}
