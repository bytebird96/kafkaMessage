package com.kafka.message.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.kafka.message.model.ChatMessage;

/**
 * Kafka Consumer를 통해 수신된 메시지 처리
 * 수신한 Kafka 메시지를 WebSocket을 통해 클라이언트에게 전달
 */
@Service
public class ChatConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     *
     * @param messagingTemplate WebSocket 메시지를 전송하기 위한 템플릿
     */
    public ChatConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Kafka로부터 수신한 메시지를 처리
     * 수신된 메시지를 ChatMessage 객체로 변환하고 WebSocket을 통해 클라이언트에게 전송
     *
     * @param messageJson 수신된 Kafka 메시지의 JSON 문자열
     */
    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void listen(String messageJson) {
        // 수신된 JSON 메시지 예시: "{\"userId\":\"user_gcsm986\",\"sender\":\"user_gcsm986\",\"content\":\"ㅁㄴㅇ\"}"

        try {
            // Kafka 메시지를 ChatMessage 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열에서 정의되지 않은 속성이 있어도 오류가 발생하지 않도록 설정
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);

            // WebSocket을 통해 "/topic/chat" 경로로 메시지를 전송합니다.
            messagingTemplate.convertAndSend("/topic/chat", chatMessage);
            System.out.println("Kafka 메시지 처리 및 WebSocket 전송: " + chatMessage);

        } catch (Exception e) {
            System.err.println("메시지 처리 오류: " + e.getMessage());
        }
    }
}
