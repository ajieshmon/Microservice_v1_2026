package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private Double amount;
    private String productCode;  // ✅ ADD
    private Integer quantity;
}
