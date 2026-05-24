package com.example.healthconsultapp;

public class UserChatItem {
    private int userId;
    private String username;
    private String fullName;
    private String lastMessage;
    private String lastMessageTime;

    public UserChatItem(int userId, String username, String fullName, String lastMessage, String lastMessageTime) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
}