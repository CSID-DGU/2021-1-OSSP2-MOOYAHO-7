package com.example.mooyaho.data_class;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    public Map<String, Boolean> users = new HashMap<>(); //채팅방의 유저들
    public Map<String, Comment> comments = new HashMap<>(); //채팅방의 대화

    public static class Comment{
        public String uid;
        public String message;
        public Object timestamp;
    }
}
