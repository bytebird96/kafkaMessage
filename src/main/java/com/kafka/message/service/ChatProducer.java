package com.kafka.message.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ChatProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ChatProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        // Kafka 메시지를 보내고 CompletableFuture를 받음
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("chat-topic", message);

        // CompletableFuture의 whenComplete 사용
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // 실패 시 처리
                System.err.println("Error sending message: " + ex.getMessage());
            } else {
                // 성공 시 처리
                System.out.println("Sent message: " + message);
            }
        });
    }
}
