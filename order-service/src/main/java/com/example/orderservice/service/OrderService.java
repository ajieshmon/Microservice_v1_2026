package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OutboxEvent;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.dto.OrderCreatedEvent;
import com.example.orderservice.kafka.OrderEventProducer;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        OutboxRepository outboxRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {

        // 1️⃣ Save Order
        Order order = Order.builder()
                .productCode(request.getProductCode())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .status(OrderStatus.PENDING.name())
                .createdAt(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);

        // 2️⃣ Create Event
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setProductCode(order.getProductCode());
        event.setQuantity(order.getQuantity());
        event.setPrice(order.getPrice());

        try {

            // 3️⃣ Save to OUTBOX instead of Kafka
            OutboxEvent outbox = new OutboxEvent();

            outbox.setAggregateType("ORDER");
            outbox.setAggregateId(order.getId());
            outbox.setEventType("order-created-topic");

            outbox.setPayload(
                    objectMapper.writeValueAsString(event)
            );

            outbox.setStatus("NEW");
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("Outbox save failed", e);
        }

        return order;
    }
}
