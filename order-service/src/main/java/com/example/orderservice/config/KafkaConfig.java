package com.example.orderservice.config;

import com.example.orderservice.dto.InventoryReservedEvent;
import com.example.orderservice.dto.PaymentRefundedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // ================= INVENTORY SUCCESS =================

    @Bean
    public ConsumerFactory<String, InventoryReservedEvent> inventoryReservedConsumerFactory() {

        JsonDeserializer<InventoryReservedEvent> deserializer =
                new JsonDeserializer<>(InventoryReservedEvent.class);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent>
    inventoryReservedKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(inventoryReservedConsumerFactory());

        // 🔥 Retry + DLQ
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    // ================= PAYMENT REFUND =================

    @Bean
    public ConsumerFactory<String, PaymentRefundedEvent> paymentRefundedConsumerFactory() {

        JsonDeserializer<PaymentRefundedEvent> deserializer =
                new JsonDeserializer<>(PaymentRefundedEvent.class);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRefundedEvent>
    paymentRefundedKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentRefundedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentRefundedConsumerFactory());

        // 🔥 Retry + DLQ
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    // ================= COMMON =================

    private Map<String, Object> consumerProps() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");

        return props;
    }
}