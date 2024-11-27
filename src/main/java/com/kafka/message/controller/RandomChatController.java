package com.kafka.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.message.model.ChatMessage;
import com.kafka.message.service.ChatProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
public class RandomChatController {
    private static final Logger log = LoggerFactory.getLogger(RandomChatController.class);

    private final ChatProducer chatProducer;

    private final SimpMessagingTemplate messagingTemplate;

    // 대기 중인 사용자 목록을 관리하는 Queue
    // ConcurrentLinkedQueue는 Thread-Safe한 자료구조로, 여러 쓰레드가 동시에 접근해도 안전하게 동작
    private final Queue<String> waitingUsers = new ConcurrentLinkedQueue<>();

    /**
     * 생성자 주입
     *
     * @param chatProducer       Kafka Producer를 통한 메시지 전송을 처리
     * @param messagingTemplate  WebSocket 메시지 전송 템플릿
     */
    @Autowired
    public RandomChatController(ChatProducer chatProducer, SimpMessagingTemplate messagingTemplate) {
        this.chatProducer = chatProducer;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 사용자가 요청을 보냈을 때 대기열에 추가하거나, 기존 대기 사용자와 매칭하여 채팅을 시작.
     *
     * @param chatMessage
     */
    @MessageMapping("/startRandomChat")
    public void startRandomChat(ChatMessage chatMessage) {
        // 사용자의 고유 ID를 메시지에서 가져옴
        String userId = chatMessage.getUserId();
        log.info("랜덤 채팅 요청 수신 - 사용자 ID: {}", userId);

        // 대기열에 사용자가 있는 경우 매칭 시도
        if (!waitingUsers.isEmpty()) {
            // 대기열에서 기존 사용자 꺼내기
            String matchedUser = waitingUsers.poll();
            log.info("매칭 성공: {} <-> {}", userId, matchedUser);

            // 매칭된 두 사용자에게 매칭 성공 메시지를 전송
            messagingTemplate.convertAndSendToUser(userId, "/topic/matching",
                    new ChatMessage(userId, "시스템", "매칭 성공!"));
            messagingTemplate.convertAndSendToUser(matchedUser, "/topic/matching",
                    new ChatMessage(matchedUser, "시스템", "매칭 성공!"));
        } else {
            // 대기열에 사용자가 없으면 현재 사용자를 대기열에 추가
            waitingUsers.add(userId);
            log.info("사용자가 대기열에 추가됨: {}", userId);

            // 사용자에게 대기 중임을 알리는 메시지 전송
            messagingTemplate.convertAndSendToUser(userId, "/topic/matching",
                    new ChatMessage(userId, "시스템", "대기 중..."));
        }
    }

    /**
     * 채팅 메시지 전송 요청
     * 클라이언트로부터 받은 메시지를 Kafka로 전송.
     *
     * @param chatMessage
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        log.info("메시지 수신: {}", chatMessage);

        // Kafka를 통해 메시지를 전송
        try {
            // 메시지를 JSON 문자열로 변환하여 Kafka Producer에 전달
            String messageJson = new ObjectMapper().writeValueAsString(chatMessage);
            chatProducer.sendMessage(messageJson);
            log.info("메시지를 Kafka로 전송: {}", messageJson);
        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
