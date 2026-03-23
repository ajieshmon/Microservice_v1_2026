package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {

        kafkaTemplate.send("order-created-topic", event);

        System.out.println("Order event published : " + event.getOrderId());
    }
}