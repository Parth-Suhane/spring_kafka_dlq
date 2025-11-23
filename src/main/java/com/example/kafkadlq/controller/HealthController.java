package com.example.kafkadlq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<String> h() {
        return ResponseEntity.ok("OK");
    }
}
