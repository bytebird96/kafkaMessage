package com.kafka.message.controller;

import com.kafka.message.service.ChatProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final ChatProducer chatProducer;

    @Autowired
    public ChatController(ChatProducer chatProducer) {
        this.chatProducer = chatProducer;
    }

    @GetMapping("/")
    public String chatPage() {
        return "chat";  // chat.html 템플릿을 반환합니다.
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        chatProducer.sendMessage(message);
        return "redirect:/";  // 메시지를 보낸 후, 다시 채팅 페이지로 리다이렉트
    }
}
