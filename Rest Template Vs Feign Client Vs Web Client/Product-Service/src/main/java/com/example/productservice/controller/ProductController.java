package com.example.productservice.controller;

import com.example.productservice.dto.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        // Simulated data
        return new Product(id, "Laptop", 75000.0, 25);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Laptop", 75000.0, 25));
        products.add(new Product(2L, "Mouse", 1200.0, 100));
        return products;
    }
}
