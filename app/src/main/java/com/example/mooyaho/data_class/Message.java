package com.example.mooyaho.data_class;

public class Message {
    String nickname;
    String message;
    String time;
    String email;

    public Message() {

    }

    public Message(String nickname, String message, String time, String email) {
        this.nickname = nickname;
        this.message = message;
        this.time = time;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
