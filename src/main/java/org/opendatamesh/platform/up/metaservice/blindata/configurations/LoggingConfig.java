package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter commonRequestLoggingFilter = new CommonsRequestLoggingFilter();
        commonRequestLoggingFilter.setIncludeClientInfo(true);
        commonRequestLoggingFilter.setIncludeQueryString(true);
        commonRequestLoggingFilter.setIncludePayload(false);
        commonRequestLoggingFilter.setMaxPayloadLength(10000);
        return commonRequestLoggingFilter;
    }

}
