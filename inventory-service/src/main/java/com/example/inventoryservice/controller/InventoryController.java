package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryRequest;
import com.example.inventoryservice.dto.PaymentCompletedEvent;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    public boolean reserveInventory(@RequestBody PaymentCompletedEvent request) {
        return inventoryService.reserveStock(request);
    }
}
