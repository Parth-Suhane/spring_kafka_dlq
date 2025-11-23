package com.example.kafkadlq.service;

import com.example.kafkadlq.dto.MessageDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DLQListener {
    private final Logger log = LoggerFactory.getLogger(DLQListener.class);

    @KafkaListener(topics = "${app.topic}.DLT", groupId = "${spring.kafka.consumer.group-id}-dlt")
    public void listenDLQ(ConsumerRecord<String, MessageDto> rec) {
        log.error("DLQ: key={} val={} partition={} offset={}", rec.key(), rec.value(), rec.partition(), rec.offset());
    }
}
