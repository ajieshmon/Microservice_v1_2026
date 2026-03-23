package com.example.gatewayservice.security;

import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // ✅ Password Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ In-memory user for login
    @Bean
    public MapReactiveUserDetailsService userDetailsService(BCryptPasswordEncoder encoder) {

        UserDetails user = User
                .withUsername("admin")
                .password(encoder.encode("123"))
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(user);
    }

    // ✅ JWT Security Filter Chain
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtUtil jwtUtil) {

        // 🔥 Proper ReactiveAuthenticationManager
        ReactiveAuthenticationManager authenticationManager = authentication -> {

            String token = authentication.getCredentials().toString();

            try {
                Claims claims = jwtUtil.validateToken(token);

                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                return Mono.just(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        )
                );
            } catch (Exception e) {
                return Mono.empty();
            }
        };

        AuthenticationWebFilter jwtFilter =
                new AuthenticationWebFilter(authenticationManager);

        // Extract token from header
        jwtFilter.setServerAuthenticationConverter(exchange -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.empty();
            }

            String token = authHeader.substring(7);

            return Mono.just(
                    new UsernamePasswordAuthenticationToken(null, token)
            );
        });

        // Stateless (VERY IMPORTANT)
        jwtFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http
                .csrf(csrf -> csrf.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}