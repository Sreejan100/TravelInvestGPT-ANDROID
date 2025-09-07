package com.example.travelinvestgpt;

public class Message {

    private String Message;
    private boolean isUser;

    public Message(String Message, boolean isUser) {
        this.Message  = Message;
        this.isUser  = isUser;
    }

    public String getText() {
        return this.Message;
    }

    public boolean isUser() {
        return this.isUser;
    }
}
