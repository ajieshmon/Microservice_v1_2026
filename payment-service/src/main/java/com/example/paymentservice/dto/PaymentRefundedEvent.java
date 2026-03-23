package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentRefundedEvent {
    private Long orderId;
}
