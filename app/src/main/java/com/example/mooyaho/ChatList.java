package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.mooyaho.Fragment.ChatFragment;

public class ChatList extends AppCompatActivity {

    ImageButton buttonHome;
    ImageButton buttonRequest;
    ImageButton buttonChatting;
    ImageButton buttonMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        buttonHome = (ImageButton) findViewById(R.id.home);
        buttonRequest = (ImageButton) findViewById(R.id.request);
        buttonChatting = (ImageButton) findViewById(R.id.chatting);
        buttonMyPage = (ImageButton) findViewById(R.id.mypage);


        buttonHome.setOnClickListener(new View.OnClickListener() { // 홈버튼
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 버튼 클릭시 DeliverRequestActivity 로 이동
                startActivity(new Intent(getApplicationContext(), DeliverRequestActivity.class));
            }
        });
        buttonChatting.setOnClickListener(new View.OnClickListener() { // 채팅창 이동 버튼
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), ChatList.class));
            }
        });
        buttonMyPage.setOnClickListener(new View.OnClickListener() { // 마이페이지 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
            }
        });


        getFragmentManager().beginTransaction().replace(R.id.fragmentlayout_chat, new ChatFragment()).commit();
    }



}