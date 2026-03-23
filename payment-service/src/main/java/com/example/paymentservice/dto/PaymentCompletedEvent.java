package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentCompletedEvent {

    private Long orderId;
    private String productCode;
    private Integer quantity;

}
