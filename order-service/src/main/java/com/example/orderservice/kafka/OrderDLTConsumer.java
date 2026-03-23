package com.example.orderservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderDLTConsumer {

    @KafkaListener(topics = "inventory-reserved-topic-dlt")
    public void handleInventoryDLQ(String msg) {
        System.out.println("🔥 DLQ (Inventory Reserved): " + msg);
    }

    @KafkaListener(topics = "payment-refunded-topic-dlt")
    public void handlePaymentDLQ(String msg) {
        System.out.println("🔥 DLQ (Payment Refunded): " + msg);
    }
}