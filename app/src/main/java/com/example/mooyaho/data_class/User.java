package com.example.mooyaho.data_class;
// commit test
public class User { // 유저 정보를 담고 있는 POJO

    public String email;
    public String nickname;

    public User(){

    }

    public User(String _email, String _nickname){
        email = _email; nickname = _nickname;
    }

    public String getEmail(){
        return email;
    }
    public String getNickname() {return nickname;}
}
