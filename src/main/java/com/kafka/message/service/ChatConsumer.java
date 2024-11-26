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

    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void listen(String messageJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);
            messagingTemplate.convertAndSend("/topic/chat", chatMessage);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}
