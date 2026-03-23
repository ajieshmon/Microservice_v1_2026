package com.example.gatewayservice.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/order")
    public Mono<Map<String,String>> fallback() {

        return Mono.just(
                Map.of(
                        "service","ORDER",
                        "status","DOWN",
                        "message","Order service temporarily unavailable"
                )
        );
    }

    @RequestMapping("/fallback/payment")
    public Mono<Map<String, String>> paymentFallback() {
        return Mono.just(
                Map.of(
                        "service", "PAYMENT",
                        "status", "DOWN",
                        "message", "Payment service temporarily unavailable"
                )
        );
    }

    @RequestMapping("/fallback/inventory")
    public Mono<Map<String, String>> inventoryFallback() {
        return Mono.just(
                Map.of(
                        "service", "INVENTORY",
                        "status", "DOWN",
                        "message", "Inventory service temporarily unavailable"
                )
        );
    }
}