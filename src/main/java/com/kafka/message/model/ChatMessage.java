package com.kafka.message.model;


public class ChatMessage {

    private String userId;  // 유저의 고유 ID
    private String sender;  // 메시지 보낸 사람 (이름 혹은 ID)
    private String content; // 메시지 내용

   // 생성자
    public ChatMessage(String userId, String sender, String content) {
        this.userId = userId;
        this.sender = sender;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


