package com.example.instana.config;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

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
