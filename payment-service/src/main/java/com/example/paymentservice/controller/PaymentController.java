package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment makePayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @PostMapping("/refund/{orderId}")
    public String refundPayment(@PathVariable Long orderId) {
        System.out.println("Refunding payment for order " + orderId);

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("REFUNDED");
        paymentRepository.save(payment);

        return "Refund successful for order " + orderId;
    }
}
