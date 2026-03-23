package com.example.orderservice.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaRetryConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition(
                                record.topic() + "-dlt",
                                record.partition()
                        )
                );

        FixedBackOff backOff = new FixedBackOff(2000L, 3);

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, backOff);

        // ✅ Retry logging
        errorHandler.setRetryListeners((record, ex, attempt) -> {
            System.out.println("Retry attempt " + attempt +
                    " for topic " + record.topic());
        });

        // ✅ Ignore non-retryable errors
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        return errorHandler;
    }
}