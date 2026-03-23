package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryFailedEvent;
import com.example.inventoryservice.dto.InventoryRequest;
import com.example.inventoryservice.dto.InventoryReservedEvent;
import com.example.inventoryservice.dto.PaymentCompletedEvent;
import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.entity.OutboxEvent;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public InventoryService(InventoryRepository inventoryRepository,
                            OutboxRepository outboxRepository,
                            ObjectMapper objectMapper) {
        this.inventoryRepository = inventoryRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public boolean reserveStock(PaymentCompletedEvent request) {

        Inventory inventory = inventoryRepository
                .findByProductCode(request.getProductCode())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ❌ IF STOCK NOT AVAILABLE → SEND FAILURE EVENT
        if (inventory.getAvailableQuantity() < request.getQuantity()) {

            try {
                InventoryFailedEvent failedEvent = new InventoryFailedEvent();
                failedEvent.setOrderId(request.getOrderId());

                OutboxEvent outbox = new OutboxEvent();

                outbox.setAggregateType("INVENTORY");
                outbox.setAggregateId(request.getOrderId());
                outbox.setEventType("inventory-failed-topic");

                outbox.setPayload(
                        objectMapper.writeValueAsString(failedEvent)
                );

                outbox.setStatus("NEW");
                outbox.setCreatedAt(LocalDateTime.now());

                outboxRepository.save(outbox);

            } catch (Exception e) {
                throw new RuntimeException("Outbox failure event error", e);
            }

            return false;
        }

        // ✅ SUCCESS FLOW
        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - request.getQuantity()
        );

        inventoryRepository.save(inventory);

        InventoryReservedEvent event = new InventoryReservedEvent();
        event.setOrderId(request.getOrderId());

        try {

            OutboxEvent outbox = new OutboxEvent();

            outbox.setAggregateType("INVENTORY");
            outbox.setAggregateId(request.getOrderId());
            outbox.setEventType("inventory-reserved-topic");

            outbox.setPayload(
                    objectMapper.writeValueAsString(event)
            );

            outbox.setStatus("NEW");
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("Outbox save failed", e);
        }

        return true;
    }
}