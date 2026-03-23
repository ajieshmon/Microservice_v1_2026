package com.example.inventoryservice.config;

import com.example.inventoryservice.dto.PaymentCompletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, PaymentCompletedEvent> paymentCompletedConsumerFactory() {

        JsonDeserializer<PaymentCompletedEvent> deserializer =
                new JsonDeserializer<>(PaymentCompletedEvent.class);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent>
    paymentCompletedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentCompletedConsumerFactory());
        return factory;
    }

    private Map<String, Object> consumerProps() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-group");

        return props;
    }
}
