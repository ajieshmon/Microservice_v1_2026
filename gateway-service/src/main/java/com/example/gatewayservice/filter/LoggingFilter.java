package com.example.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        System.out.println("Incoming Request: "
                + exchange.getRequest().getURI());

        return chain.filter(exchange);
    }
}
