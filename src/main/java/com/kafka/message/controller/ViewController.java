package com.kafka.message.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


//http://localhost:9090/kafkaTest/view/main
@Controller
@RequestMapping("/view")
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);

    // 메인 화면 (채팅 페이지)
    @GetMapping("/main")
    public String chatPage() {
        return "main";  // chat.html 템플릿을 반환합니다.
    }

    /**
     * 랜덤채팅 1:1 페이지로 이동
     * @return
     */
    @GetMapping("/randomChat")
    public String randomChat(HttpSession session) {
        // 세션에 userId가 없으면 새로 설정
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            userId = "guest_" + System.currentTimeMillis(); // 임시 userId
            session.setAttribute("userId", userId);
        }
        return "randomChat";  // 랜덤 채팅 페이지로 리다이렉트
    }
}

