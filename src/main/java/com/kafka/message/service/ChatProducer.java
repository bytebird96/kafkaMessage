package com.kafka.message.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer를 이용해 메시지를 Kafka 토픽에 전송
 */
@Service
public class ChatProducer {

    // Kafka 메시지 전송을 위한 KafkaTemplate 객체
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 메시지를 JSON 형식으로 직렬화하기 위한 ObjectMapper 객체
    private final ObjectMapper objectMapper;

    /**
     *
     * @param kafkaTemplate
     * @param objectMapper
     */
    public ChatProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 주어진 메시지를 Kafka에 전송
     *
     * @param message 전송할 메시지 (JSON 문자열)
     */
    public void sendMessage(String message) {
        try {
            // 메시지를 Kafka 토픽(chat-topic)에 비동기로 전송합니다.
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("chat-topic", message);

            // 전송 결과 처리: 성공 시 로그 출력, 실패 시 오류 메시지 출력
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    // 전송 실패 시 예외 로그 출력
                    System.err.println("Error sending message: " + ex.getMessage());
                } else {
                    // 전송 성공 시 로그 출력
                    System.out.println("Sent message: " + message);
                }
            });
        } catch (Exception e) {
            // 메시지 직렬화 중 발생한 예외 처리
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }
}
