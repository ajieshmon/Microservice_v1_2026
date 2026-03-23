package com.example.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(columnNames = "orderId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Double amount;

    private String status; // SUCCESS, FAILED

    private String transactionId;

    private LocalDateTime createdAt;
}
