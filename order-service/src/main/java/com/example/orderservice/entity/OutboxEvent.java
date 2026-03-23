package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private Long aggregateId;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status;

    private LocalDateTime createdAt;
}
