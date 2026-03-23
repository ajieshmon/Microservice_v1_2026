package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class OrderCreatedEvent {

    private Long orderId;
    private String productCode;
    private Integer quantity;
    private Double price;

    // getters setters
}
