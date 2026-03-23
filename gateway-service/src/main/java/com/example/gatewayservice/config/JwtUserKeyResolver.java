package com.example.gatewayservice.config;

import com.example.gatewayservice.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class JwtUserKeyResolver {

    private final JwtUtil jwtUtil;

    public JwtUserKeyResolver(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public KeyResolver userKeyResolver() {

        return exchange -> {

            String authHeader = exchange
                    .getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.just("anonymous");
            }

            try {

                String token = authHeader.substring(7);

                Claims claims = jwtUtil.validateToken(token);

                String username = claims.getSubject();

                return Mono.just(username);

            } catch (Exception e) {

                return Mono.just("anonymous");
            }
        };
    }
}