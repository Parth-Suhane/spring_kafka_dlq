package com.example.kafkadlq.service;

import com.example.kafkadlq.dto.MessageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final String topic;

    public ProducerService(KafkaTemplate<String, MessageDto> kafkaTemplate,
                           @Value("${app.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(MessageDto msg) {
        kafkaTemplate.send(topic, msg.getId(), msg);
    }
}
