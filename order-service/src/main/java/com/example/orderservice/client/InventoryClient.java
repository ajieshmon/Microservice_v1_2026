package com.example.orderservice.client;

import com.example.orderservice.dto.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "http://localhost:8083")
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    Boolean reserveInventory(@RequestBody InventoryRequest request);
}
