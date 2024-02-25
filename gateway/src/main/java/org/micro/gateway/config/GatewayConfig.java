package org.micro.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("client", r -> r
                        .path("/start/hello")
                        .uri("lb://eclient"))
                .route("client", r -> r
                        .path("/start/login")
                        .uri("lb://eclient"))
                .build();
    }
}