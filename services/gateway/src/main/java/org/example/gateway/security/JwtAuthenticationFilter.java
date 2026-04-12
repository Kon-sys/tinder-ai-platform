package org.example.gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    private static final PathPatternParser PATTERN_PARSER = new PathPatternParser();

    private static final List<PathPattern> OPEN_ENDPOINTS = List.of(
            PATTERN_PARSER.parse("/api/auth/register"),
            PATTERN_PARSER.parse("/api/auth/login"),
            PATTERN_PARSER.parse("/api/auth/refresh"),
            PATTERN_PARSER.parse("/actuator/health")
    );

    private static final List<PathPattern> USER_ENDPOINTS = List.of(
            PATTERN_PARSER.parse("/api/profile/**"),
            PATTERN_PARSER.parse("/api/feed/**"),
            PATTERN_PARSER.parse("/api/swipes/**"),
            PATTERN_PARSER.parse("/api/matches/**"),
            PATTERN_PARSER.parse("/api/chats/**"),
            PATTERN_PARSER.parse("/api/notifications/**")
    );

    private static final List<PathPattern> ADMIN_ENDPOINTS = List.of(
            PATTERN_PARSER.parse("/api/admin/**")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        PathContainer pathContainer = PathContainer.parsePath(path);

        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        if (matchesAny(pathContainer, OPEN_ENDPOINTS)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtService.extractClaims(token);
        } catch (Exception e) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        String login = claims.getSubject();
        String role = claims.get("role", String.class);

        if (login == null || login.isBlank()) {
            return unauthorized(exchange, "Login not found in token");
        }

        if (role == null || role.isBlank()) {
            return forbidden(exchange, "Role not found in token");
        }

        if (!hasAccess(pathContainer, role)) {
            return forbidden(exchange, "Access denied");
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Login", login)
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean hasAccess(PathContainer path, String role) {
        if ("ROLE_ADMIN".equals(role)) {
            return matchesAny(path, ADMIN_ENDPOINTS) || matchesAny(path, USER_ENDPOINTS);
        }

        if ("ROLE_USER".equals(role)) {
            return matchesAny(path, USER_ENDPOINTS);
        }

        return false;
    }

    private boolean matchesAny(PathContainer path, List<PathPattern> patterns) {
        return patterns.stream().anyMatch(pattern -> pattern.matches(path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"error\":\"" + escapeJson(message) + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"error\":\"" + escapeJson(message) + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    private String escapeJson(String value) {
        return value.replace("\"", "\\\"");
    }

    @Override
    public int getOrder() {
        return -1;
    }
}