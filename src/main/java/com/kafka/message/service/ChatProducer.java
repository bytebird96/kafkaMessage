package com.kafka.message.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

@Service
public class ChatProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ChatProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(String message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("chat-topic", messageJson);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    System.err.println("Error sending message: " + ex.getMessage());
                } else {
                    System.out.println("Sent message: " + messageJson);
                }
            });
        } catch (Exception e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }
}

