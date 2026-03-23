package com.example.orderservice.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private String productCode;
    private Integer quantity;
}
