package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productCode;

    private Integer quantity;

    private Double price;

    private String status; // CREATED, CONFIRMED, CANCELLED

    private LocalDateTime createdAt;
}
