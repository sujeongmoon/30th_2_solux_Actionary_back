package com.req2res.actionarybe.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient downloadClient() {
        int maxBytes = 20 * 1024 * 1024; // 20MB
        return WebClient.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(maxBytes))
                .build();
    }
}
