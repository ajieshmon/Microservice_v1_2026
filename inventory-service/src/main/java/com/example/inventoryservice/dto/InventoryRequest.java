package com.example.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private String productCode;
    private Integer quantity;
}
