package com.kafka.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.kafka.message.model.ChatMessage;

@Service
public class ChatConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 채팅 전송
     * @param messageJson
     */
    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void listen(String messageJson) {
        try {
            // Kafka 메시지를 ChatMessage 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);

            // WebSocket을 통해 클라이언트로 전송
            messagingTemplate.convertAndSend("/topic/chat", chatMessage);
            System.out.println("Kafka 메시지 처리 및 WebSocket 전송: " + chatMessage);

        } catch (Exception e) {
            System.err.println("메시지 처리 오류: " + e.getMessage());
        }
    }
}
