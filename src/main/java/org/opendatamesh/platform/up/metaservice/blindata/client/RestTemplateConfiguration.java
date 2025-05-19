package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;

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
            HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
            HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

            while (elementIterator.hasNext()) {
                HeaderElement element = elementIterator.nextElement();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000; // convert to ms
                }
            }

            return TimeUnit.SECONDS.toMillis(20);
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
                    pool.closeExpiredConnections();
                    pool.closeIdleConnections(20, TimeUnit.SECONDS);
                }
            }
        };
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        // Configure Apache HttpClient with no connection pooling
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60_000) // time to open a server connection
                .setConnectionRequestTimeout(60_000) // time to request connection from the pool
                .setSocketTimeout(60_000)//  time between data packets
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
