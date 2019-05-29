package com.instant.doctor.models;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String sessionId;



    private List<Message> messages;


    public Chat() {
//        this.messages = new ArrayList<>();
    }

    public Chat(String sessionId, List<Message> messages) {
        this.sessionId = sessionId;
        this.messages = messages;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
