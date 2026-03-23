package com.example.orderservice.dto;


import lombok.Data;

@Data
public class OrderCreatedEvent {

    private Long orderId;
    private String productCode;
    private int quantity;
    private Double price;
}
