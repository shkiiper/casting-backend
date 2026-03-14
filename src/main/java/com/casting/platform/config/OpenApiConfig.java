package com.casting.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.publicUrl:http://localhost:8080}")
    private String publicUrl;

    @Bean
    public OpenAPI castingPlatformOpenAPI() {
        Server server = new Server()
                .url(publicUrl)
                .description("Casting Platform API");

        Info info = new Info()
                .title("Casting Platform API")
                .version("1.0.0")
                .description("REST API для кастингов, профилей и подписок")
                .contact(new Contact().name("Backend").email("no-reply@example.com"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
