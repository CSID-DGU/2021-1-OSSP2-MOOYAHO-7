package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mooyaho.Fragment.ChatFragment;

public class ChatList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        getFragmentManager().beginTransaction().replace(R.id.fragmentlayout_chat, new ChatFragment()).commit();
    }
}