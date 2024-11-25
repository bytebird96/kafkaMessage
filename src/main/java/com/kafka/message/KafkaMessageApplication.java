package com.kafka.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
public class KafkaMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaMessageApplication.class, args);
        // 서버가 시작된 후 브라우저 열기
        try {
            //실행 브라우저 열리도록 변경
            URI uri = new URI("http://localhost:9090"); // 서버 주소
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri); // 기본 웹 브라우저 열기
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
