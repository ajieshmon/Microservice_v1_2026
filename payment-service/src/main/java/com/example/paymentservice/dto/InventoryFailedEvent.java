package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class InventoryFailedEvent {
    private Long orderId;
}
