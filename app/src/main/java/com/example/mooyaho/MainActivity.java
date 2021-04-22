package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setButtonClickListener();
    }

    private void initView() {
        button = (Button) findViewById(R.id.button);
    }

    private void setButtonClickListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DeliverRequestActivity.class));       // 버튼 클릭시 DeliverRequestActivity 로 이동
            }
        });
    }
}