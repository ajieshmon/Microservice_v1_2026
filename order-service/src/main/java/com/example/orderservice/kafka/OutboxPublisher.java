package com.example.orderservice.kafka;

import com.example.orderservice.entity.OutboxEvent;
import com.example.orderservice.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           ObjectMapper objectMapper) {

        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 3000)
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxRepository.findByStatus("NEW");

        for (OutboxEvent event : events) {

            try {

                // 🔥 Convert JSON String → Object
                Object payloadObject = objectMapper.readValue(
                        event.getPayload(),
                        Object.class   // or specific DTO
                );

                kafkaTemplate.send(
                        event.getEventType(),
                        payloadObject   // ✅ send object, not string
                );

                event.setStatus("PUBLISHED");
                outboxRepository.save(event);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Kafka publish failed");
            }
        }
    }
}
