package org.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-register", r -> r
                        .path("/api/auth/register")
                        .uri("http://localhost:8081"))
                .route("auth-login", r -> r
                        .path("/api/auth/login")
                        .uri("http://localhost:8081"))
                .route("auth-refresh", r -> r
                        .path("/api/auth/refresh")
                        .uri("http://localhost:8081"))
                .route("profile-service", r -> r
                        .path("/api/profile/**")
                        .uri("http://localhost:8082"))
                .route("recommendation-service", r -> r
                        .path("/api/feed/**")
                        .uri("http://localhost:8083"))
                .route("swipe-service", r -> r
                        .path("/api/swipes/**")
                        .uri("http://localhost:8084"))
                .route("match-service", r -> r
                        .path("/api/matches/**", "/api/chats/**")
                        .uri("http://localhost:8085"))
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("http://localhost:8086"))
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .uri("http://localhost:8087"))
                .build();
    }
}
