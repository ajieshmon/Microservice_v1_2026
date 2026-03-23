package com.example.orderservice.client;

import com.example.orderservice.dto.PaymentRequest;
import com.example.orderservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8082")
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse makePayment(@RequestBody PaymentRequest request);

    // Step 2: Compensation (Refund)
    @PostMapping("/api/payments/refund/{orderId}")
    String refundPayment(@PathVariable("orderId") Long orderId);

}
