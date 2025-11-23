package com.example.kafkadlq.controller;

import com.example.kafkadlq.dto.MessageDto;
import com.example.kafkadlq.service.ProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProducerController {

    private final ProducerService svc;

    public ProducerController(ProducerService svc) {
        this.svc = svc;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody MessageDto msg) {
        svc.sendMessage(msg);
        return ResponseEntity.accepted().body("Sent: " + msg.getId());
    }
}
