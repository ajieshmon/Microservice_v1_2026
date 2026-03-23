package com.example.paymentservice.config;

import com.example.paymentservice.dto.InventoryFailedEvent;
import com.example.paymentservice.dto.OrderCreatedEvent;
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

    // ================= ORDER CREATED =================

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory() {

        JsonDeserializer<OrderCreatedEvent> jsonDeserializer =
                new JsonDeserializer<>(OrderCreatedEvent.class);

        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    orderCreatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(orderCreatedConsumerFactory());
        return factory;
    }

    // ================= INVENTORY FAILED =================

    @Bean
    public ConsumerFactory<String, InventoryFailedEvent> inventoryFailedConsumerFactory() {

        JsonDeserializer<InventoryFailedEvent> jsonDeserializer =
                new JsonDeserializer<>(InventoryFailedEvent.class);

        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryFailedEvent>
    inventoryFailedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, InventoryFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(inventoryFailedConsumerFactory());
        return factory;
    }

    // ================= COMMON CONFIG =================

    private Map<String, Object> consumerProps() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-group");

        // Recommended configs
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return props;
    }
}