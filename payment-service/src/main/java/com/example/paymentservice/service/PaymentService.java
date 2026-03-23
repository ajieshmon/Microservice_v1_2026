package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentCompletedEvent;
import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.entity.OutboxEvent;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.OutboxRepository;
import com.example.paymentservice.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          OutboxRepository outboxRepository,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {

        // 1️⃣ Idempotency check
        Optional<Payment> existing =
                paymentRepository.findByOrderId(request.getOrderId());

        if (existing.isPresent()) {
            System.out.println("Idempotent call detected. Returning existing payment.");
            return existing.get();
        }

        // 2️⃣ Save Payment
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status("SUCCESS")
                .transactionId(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // 3️⃣ Create Event
        PaymentCompletedEvent event = new PaymentCompletedEvent();
        event.setOrderId(payment.getOrderId());
        event.setProductCode(request.getProductCode());
        event.setQuantity(request.getQuantity());

        try {

            // 4️⃣ Save to OUTBOX
            OutboxEvent outbox = new OutboxEvent();

            outbox.setAggregateType("PAYMENT");
            outbox.setAggregateId(payment.getId());
            outbox.setEventType("payment-completed-topic");

            outbox.setPayload(
                    objectMapper.writeValueAsString(event)
            );

            outbox.setStatus("NEW");
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("Outbox save failed", e);
        }

        return payment;
    }
}