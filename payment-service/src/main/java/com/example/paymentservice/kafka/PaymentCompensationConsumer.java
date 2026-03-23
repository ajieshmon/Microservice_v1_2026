package com.example.paymentservice.kafka;

import com.example.paymentservice.dto.InventoryFailedEvent;
import com.example.paymentservice.dto.PaymentRefundedEvent;
import com.example.paymentservice.entity.OutboxEvent;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.OutboxRepository;
import com.example.paymentservice.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentCompensationConsumer {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentCompensationConsumer(PaymentRepository paymentRepository,
                                       OutboxRepository outboxRepository,
                                       ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "inventory-failed-topic",
            containerFactory = "inventoryFailedKafkaListenerContainerFactory"
    )
    public void handleInventoryFailure(InventoryFailedEvent event) {

        System.out.println("Inventory failed. Initiating refund for order "
                + event.getOrderId());

        // 1️⃣ Find payment
        Payment payment = paymentRepository
                .findByOrderId(event.getOrderId())
                .orElseThrow();

        // 2️⃣ Update status
        payment.setStatus("REFUNDED");
        paymentRepository.save(payment);

        try {
            // 3️⃣ Create refund event
            PaymentRefundedEvent refundEvent = new PaymentRefundedEvent();
            refundEvent.setOrderId(event.getOrderId());

            // 4️⃣ Save to OUTBOX
            OutboxEvent outbox = new OutboxEvent();

            outbox.setAggregateType("PAYMENT");
            outbox.setAggregateId(payment.getId());
            outbox.setEventType("payment-refunded-topic");

            outbox.setPayload(
                    objectMapper.writeValueAsString(refundEvent)
            );

            outbox.setStatus("NEW");
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("Refund Outbox failed", e);
        }
    }
}
