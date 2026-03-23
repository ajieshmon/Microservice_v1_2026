package com.example.inventoryservice.dto;


import lombok.Data;

@Data
public class PaymentCompletedEvent {

    private Long orderId;
    private String productCode;
    private Integer quantity;

    // getters setters
}
