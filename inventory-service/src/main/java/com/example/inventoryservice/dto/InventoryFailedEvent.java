package com.example.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryFailedEvent {
    private Long orderId;
}
