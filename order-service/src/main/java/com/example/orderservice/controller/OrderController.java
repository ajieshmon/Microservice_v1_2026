package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Order placeOrder(@RequestBody OrderRequest request) {
//        return orderService.createOrder(request);
//    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public Order placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }
}
