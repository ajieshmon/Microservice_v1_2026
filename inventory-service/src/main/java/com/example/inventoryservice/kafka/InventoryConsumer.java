package com.example.inventoryservice.kafka;

import com.example.inventoryservice.dto.InventoryReservedEvent;
import com.example.inventoryservice.dto.PaymentCompletedEvent;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "payment-completed-topic",
            containerFactory = "paymentCompletedKafkaListenerContainerFactory"
    )
    public void consumePaymentCompletedEvent(PaymentCompletedEvent event) {

        boolean reserved = inventoryService.reserveStock(event);

        if (!reserved) {
            System.out.println("Inventory failed for order "
                    + event.getOrderId());
        }

        // ❌ No Kafka here
    }
}
