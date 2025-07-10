package com.example.udtbe.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "반딧불 API API",
                description = "반딧불 API 명세서",
                version = "v.1.0"),
        servers = {
                @Server(url = "https://dev.banditbool.com", description = "Deploy Server URL"),
                @Server(url = "http://localhost:8080", description = "Local Host URL")}
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(createApiInfo());
    }

    private io.swagger.v3.oas.models.info.Info createApiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("반딧불 API")
                .description("반딧불 API 명세서")
                .version("1.0.0");
    }
}

