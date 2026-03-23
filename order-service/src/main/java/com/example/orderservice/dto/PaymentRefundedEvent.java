package com.example.orderservice.dto;

import lombok.Data;

@Data
public class PaymentRefundedEvent {
    private Long orderId;
}
