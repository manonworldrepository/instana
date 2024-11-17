package com.example.instana.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi storeOpenApi() {
        String[] paths = { "/stats/**" };

        return GroupedOpenApi
                .builder()
                .group("Instana Distributed Tracing")
                .pathsToMatch(paths)
                .build();
    }
}
