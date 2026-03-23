package com.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String productCode;
    private Integer quantity;
    private Double price;
}
