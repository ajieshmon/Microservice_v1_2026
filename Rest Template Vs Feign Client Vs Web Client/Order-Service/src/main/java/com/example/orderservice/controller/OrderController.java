package com.example.orderservice.controller;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final ProductClient productClient;

    public OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/product/{id}")
    public Product getProductDetails(@PathVariable Long id) {
        return productClient.getProductById(id);
    }

    @GetMapping("/products")
    public List<Product> fetchAllProducts() {
        return productClient.getAllProducts();
    }
}
