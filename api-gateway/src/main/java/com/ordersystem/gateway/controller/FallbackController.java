package com.ordersystem.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public Mono<Map<String, String>> userServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User service is temporarily unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/products")
    public Mono<Map<String, String>> productServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product service is temporarily unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/orders")
    public Mono<Map<String, String>> orderServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order service is temporarily unavailable. Please try again later.");
        return Mono.just(response);
    }
}
