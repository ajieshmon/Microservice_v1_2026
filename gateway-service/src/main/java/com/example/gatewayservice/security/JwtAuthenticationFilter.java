package com.example.gatewayservice.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Allow auth endpoints
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {

            // 🔥 Step 1: Validate token
            Claims claims = jwtUtil.validateToken(token);

            // 🔥 Step 2: Extract JTI
            String jti = claims.getId();

            // 🔥 Step 3: Check blacklist
            return tokenBlacklistService.isBlacklisted(jti)
                    .flatMap(blacklisted -> {

                        if (blacklisted) {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        String username = claims.getSubject();
                        String role = claims.get("role", String.class);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        List.of(new SimpleGrantedAuthority(role))
                                );

                        return chain.filter(exchange)
                                .contextWrite(
                                        ReactiveSecurityContextHolder
                                                .withAuthentication(authentication)
                                );
                    });

        } catch (Exception e) {

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}