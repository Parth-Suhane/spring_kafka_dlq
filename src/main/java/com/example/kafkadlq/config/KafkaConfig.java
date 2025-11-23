package com.example.kafkadlq.config;

import java.util.*;

import com.example.kafkadlq.dto.MessageDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.serializer.*;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${retry.max-attempts:3}")
    private Long maxAttempts;

    @Value("${retry.backoff-ms:2000}")
    private Long backoffMs;

    @Bean
    public ProducerFactory<String, MessageDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, MessageDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, MessageDto> consumerFactory() {
        JsonDeserializer<MessageDto> deserializer = new JsonDeserializer<>(MessageDto.class);
        deserializer.addTrustedPackages("com.example.*");

        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "app-consumer-group");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<String, MessageDto> kafkaListenerContainerFactory(
            KafkaTemplate<String, MessageDto> template) {

        org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<String, MessageDto> factory = new org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<String, MessageDto>();
        factory.setConsumerFactory(consumerFactory());

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (cr, e) -> new org.apache.kafka.common.TopicPartition(cr.topic() + ".DLT", cr.partition()));

        FixedBackOff backOff = new FixedBackOff(backoffMs, Math.max(0, maxAttempts - 1));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
