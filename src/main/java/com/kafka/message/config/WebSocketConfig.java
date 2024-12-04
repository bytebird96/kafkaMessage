package com.kafka.message.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스입니다.
 * 이 클래스는 Spring에서 WebSocket 메시지 브로커를 활성화하고 구성합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정
     * 메시지 라우팅을 처리하기 위해 SimpleBroker와 애플리케이션 목적지를 설정
     *
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic", "/queue", "/user" 경로를 SimpleBroker에 등록
        // 해당 경로로 전달된 메시지는 브로커를 통해 클라이언트로 전달됩니다.
        config.enableSimpleBroker("/topic", "/queue", "/user");

        // "/app" 경로로 시작하는 메시지는 애플리케이션으로 라우팅되도록 설정
        // 컨트롤러에서 처리하는 메시지의 prefix를 설정합니다.
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * WebSocket 엔드포인트 등록 메서드입니다.
     * 클라이언트가 WebSocket 서버에 연결하기 위한 엔드포인트를 설정합니다.
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * "/chat" 경로를 WebSocket 엔드포인트로 등록
         * SockJS를 활성화하여 WebSocket을 지원하지 않는 브라우저에서도 사용할 수 있도록 설정
         * 실제 호출 경로 "/kafkaTest/chat"
         * Whale 브라우저에서 WebSocket이 동작하지 않는 문제로 SockJS를 추가
         */
        registry.addEndpoint("/chat").withSockJS();

        // registry.addEndpoint("/chat");
    }
}
