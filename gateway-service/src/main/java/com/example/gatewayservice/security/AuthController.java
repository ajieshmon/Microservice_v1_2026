package com.example.gatewayservice.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(ReactiveUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public Mono<Map<String, String>> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        return userDetailsService.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {

                    String role = user.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority();

                    String accessToken = jwtUtil.generateToken(username, role);
                    String refreshToken = jwtUtil.generateRefreshToken(username);

                    return Map.of(
                            "accessToken", accessToken,
                            "refreshToken", refreshToken
                    );
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }

    @PostMapping("/refresh")
    public Mono<Map<String, String>> refresh(
            @RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        try {

            Claims claims = jwtUtil.validateToken(refreshToken);

            if (!"REFRESH".equals(claims.get("type"))) {
                return Mono.error(new RuntimeException("Invalid refresh token"));
            }

            String jti = claims.getId();
            String username = claims.getSubject();

            long expirySeconds =
                    (claims.getExpiration().getTime()
                            - System.currentTimeMillis()) / 1000;

            return tokenBlacklistService.isBlacklisted(jti)
                    .flatMap(blacklisted -> {

                        if (blacklisted) {
                            return Mono.error(
                                    new RuntimeException("Refresh token already used"));
                        }

                        return tokenBlacklistService
                                .blacklistToken(jti, expirySeconds)
                                .then(userDetailsService.findByUsername(username))
                                .map(user -> {

                                    String role = user.getAuthorities()
                                            .iterator()
                                            .next()
                                            .getAuthority();

                                    String newAccessToken =
                                            jwtUtil.generateToken(username, role);

                                    String newRefreshToken =
                                            jwtUtil.generateRefreshToken(username);

                                    return Map.of(
                                            "accessToken", newAccessToken,
                                            "refreshToken", newRefreshToken
                                    );
                                });
                    });

        } catch (Exception e) {
            return Mono.error(new RuntimeException("Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public Mono<String> logout(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);

            long expiryMillis =
                    claims.getExpiration().getTime() - System.currentTimeMillis();

            long expirySeconds = expiryMillis / 1000;

            return tokenBlacklistService
                    .blacklistToken(token, expirySeconds)
                    .thenReturn("Logged out successfully");

        } catch (Exception e) {
            return Mono.error(new RuntimeException("Invalid token"));
        }
    }
}