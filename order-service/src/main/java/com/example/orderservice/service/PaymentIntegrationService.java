package com.example.orderservice.service;

import com.example.orderservice.client.PaymentClient;
import com.example.orderservice.dto.PaymentRequest;
import com.example.orderservice.dto.PaymentResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PaymentIntegrationService {

    private final PaymentClient paymentClient;

    public PaymentIntegrationService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Bulkhead(name = "paymentBulkhead", type = Bulkhead.Type.THREADPOOL)
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentService")
    public CompletableFuture<PaymentResponse> callPayment(PaymentRequest request) {

        return CompletableFuture.supplyAsync(() ->
                paymentClient.makePayment(request)
        );
    }

    public CompletableFuture<PaymentResponse> paymentFallback(
            PaymentRequest request, Throwable ex) {

        System.out.println("Payment fallback triggered: " + ex.getMessage());

        PaymentResponse response = new PaymentResponse();
        response.setStatus("FAILED");

        return CompletableFuture.completedFuture(response);
    }
}

