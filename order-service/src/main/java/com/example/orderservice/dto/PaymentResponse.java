package com.example.orderservice.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String status;
    private String transactionId;
}