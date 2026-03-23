package com.example.gatewayservice.security;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class TokenBlacklistService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public TokenBlacklistService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Void> blacklistToken(String jti, long expirySeconds) {

        return redisTemplate
                .opsForValue()
                .set("blacklist:" + jti,
                        "BLACKLISTED",
                        Duration.ofSeconds(expirySeconds))
                .then();
    }

    public Mono<Boolean> isBlacklisted(String jti) {
        return redisTemplate.hasKey("blacklist:" + jti);
    }
}