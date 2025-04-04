package com.tenpo.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:API}")
    private String appName;

    @Value("${spring.application.version:1.0.0}")
    private String appVersion;

    @Value("${spring.application.description:API Documentation}")
    private String appDescription;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url(contextPath).description("Default Server URL")
                ))
                .info(new Info()
                        .title(appName + " API")
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("https://example.com/terms")
                        .contact(new Contact()
                                .name("API Support")
                                .url("https://example.com/contact")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }

}