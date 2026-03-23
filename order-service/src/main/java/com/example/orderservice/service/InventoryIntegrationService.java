package com.example.orderservice.service;

import com.example.orderservice.client.InventoryClient;
import com.example.orderservice.client.PaymentClient;
import com.example.orderservice.dto.InventoryRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;


@Service
public class InventoryIntegrationService {

    private final InventoryClient inventoryClient;

    public InventoryIntegrationService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "inventoryFallback")
    @Retry(name = "inventoryService")
    public Boolean callInventory(InventoryRequest request) {
        return inventoryClient.reserveInventory(request);
    }

    public Boolean inventoryFallback(InventoryRequest request, Throwable ex) {

        System.out.println("Inventory fallback triggered");

        return false;
    }
}
