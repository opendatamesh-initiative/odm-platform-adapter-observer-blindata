package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true); // Log IP client and session ID
        loggingFilter.setIncludeQueryString(true); // Log query params
        loggingFilter.setIncludePayload(true); // Log request payload
        loggingFilter.setMaxPayloadLength(10000); // request payload length
        loggingFilter.setIncludeHeaders(true); // Log headers
        return loggingFilter;
    }

}
