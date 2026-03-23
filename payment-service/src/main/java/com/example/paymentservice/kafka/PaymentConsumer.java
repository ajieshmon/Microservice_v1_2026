package com.example.paymentservice.kafka;

import com.example.paymentservice.dto.OrderCreatedEvent;
import com.example.paymentservice.dto.PaymentCompletedEvent;
import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private final PaymentService paymentService;

    public PaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "order-created-topic",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        System.out.println("Payment processing for order " + event.getOrderId());

        // 1️⃣ Build request
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(event.getOrderId());
        request.setAmount(event.getPrice());
        request.setProductCode(event.getProductCode());
        request.setQuantity(event.getQuantity());


        // 2️⃣ Call service (this will save Payment + Outbox)
        paymentService.processPayment(request);

        // ❌ DO NOT publish Kafka here
        // OutboxPublisher will handle it
    }
}