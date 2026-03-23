package com.example.orderservice.kafka;

import com.example.orderservice.dto.InventoryReservedEvent;
import com.example.orderservice.dto.PaymentRefundedEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderConsumer {

    private final OrderRepository orderRepository;

    public OrderConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // ✅ SUCCESS FLOW
    @Transactional
    @KafkaListener(
            topics = "inventory-reserved-topic",
            groupId = "order-group",
            containerFactory = "inventoryReservedKafkaListenerContainerFactory"
    )
    public void handleInventoryReserved(InventoryReservedEvent event) {

        System.out.println("Received inventory reserved for order " + event.getOrderId());

        // ❌ FORCE ERROR
//        throw new RuntimeException("Intentional failure for DLQ testing");

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow();

        // ✅ Idempotency check
        if ("CONFIRMED".equals(order.getStatus())) {
            return;
        }

        order.setStatus("CONFIRMED");
        orderRepository.save(order);

        System.out.println("Order CONFIRMED: " + event.getOrderId());
    }

    // 🔴 FAILURE FLOW (Compensation)
    @Transactional
    @KafkaListener(
            topics = "payment-refunded-topic",
            groupId = "order-group",
            containerFactory = "paymentRefundedKafkaListenerContainerFactory"
    )
    public void handlePaymentRefunded(PaymentRefundedEvent event) {

        System.out.println("Received payment refund for order " + event.getOrderId());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow();

        // ✅ Idempotency check
        if ("CANCELLED".equals(order.getStatus())) {
            return;
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        System.out.println("Order CANCELLED: " + event.getOrderId());
    }
}