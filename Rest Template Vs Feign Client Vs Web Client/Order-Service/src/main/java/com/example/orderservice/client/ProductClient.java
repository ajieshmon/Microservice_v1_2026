package com.example.orderservice.client;


import com.example.orderservice.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", url = "${product.service.url}")  // read from application.yml
public interface ProductClient {

    @GetMapping("/products")
    List<Product> getAllProducts();

    @GetMapping("/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}
