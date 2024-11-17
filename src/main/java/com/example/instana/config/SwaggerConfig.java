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

    @Bean
    public OpenApiCustomizer customiseOpenApi() {
        return openApi -> openApi.getPaths()
                .forEach((path, pathItem) -> pathItem.readOperations().forEach(operation -> {
                    if (path.equals("/stats") && operation.getOperationId() != null && operation.getOperationId().equals("index")) {
                        RequestBody requestBody = new RequestBody()
                            .required(true)
                            .content(new Content().addMediaType("multipart/form-data",
                                new io.swagger.v3.oas.models.media.MediaType().schema(new Schema<>()
                                    .type("object")
                                    .addProperty("file", new Schema<>().type("string").format("binary"))
                                )
                            ));
                        operation.setRequestBody(requestBody);
                    }
                }));
    }
}
