package com.kafka.message.controller;

import com.kafka.message.model.ChatMessage;
import com.kafka.message.service.ChatProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.Queue;

@Controller
public class ChatController {

    private final ChatProducer chatProducer;
    private final SimpMessagingTemplate messagingTemplate;
    private Queue<String> waitingUsers = new LinkedList<>();  // 대기 중인 사용자 목록

    @Autowired
    public ChatController(ChatProducer chatProducer, SimpMessagingTemplate messagingTemplate) {
        this.chatProducer = chatProducer;
        this.messagingTemplate = messagingTemplate;
    }

    // 메인 화면 (채팅 페이지)
    @GetMapping("/")
    public String chatPage() {
        return "main";  // chat.html 템플릿을 반환합니다.
    }

    // 메시지 전송 (일반적인 메시지)
    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        chatProducer.sendMessage(message);  // 메시지를 Kafka로 전송 (혹은 다른 방법)
        return "redirect:/";  // 메시지를 보낸 후, 다시 채팅 페이지로 리다이렉트
    }

    // 랜덤 채팅 시작 요청 처리
    @MessageMapping("/startRandomChat")
    public void startRandomChat(ChatMessage chatMessage) {
        String userId = chatMessage.getUserId();  // 사용자 ID 받아오기

        if (!waitingUsers.isEmpty()) {
            // 대기 중인 사용자와 매칭하여 채팅 시작
            String matchedUser = waitingUsers.poll();  // 대기 중인 사용자 꺼내기
            messagingTemplate.convertAndSendToUser(userId, "/topic/chat", new ChatMessage(userId, "시스템", "매칭 성공!"));
            messagingTemplate.convertAndSendToUser(matchedUser, "/topic/chat", new ChatMessage(matchedUser, "시스템", "매칭 성공!"));
        } else {
            // 대기 사용자 목록에 추가
            waitingUsers.add(userId);
            messagingTemplate.convertAndSendToUser(userId, "/topic/chat", new ChatMessage(userId, "시스템", "대기 중..."));
        }
    }

    // 메시지 전송 처리
    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat", message);  // 모든 사용자에게 메시지 전송
    }
}
