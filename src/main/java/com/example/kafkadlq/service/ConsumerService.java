package com.example.kafkadlq.service;

import com.example.kafkadlq.dto.MessageDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private final ThirdPartyService thirdParty;

    public ConsumerService(ThirdPartyService thirdParty) {
        this.thirdParty = thirdParty;
    }

    @KafkaListener(topics = "${app.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, MessageDto> rec) {
        log.info("Received: key={} val={}", rec.key(), rec.value());
        String resp = thirdParty.callThirdParty(rec.value());
        log.info("Third-party response: {}", resp);
    }
}
