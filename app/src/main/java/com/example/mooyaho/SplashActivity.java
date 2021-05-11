package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SplashActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ani;
        ani = findViewById(R.id.imageView2);
        Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.activity_splash_imageview);
        ani.setAnimation(anim1);

        TextView text;
        text = findViewById(R.id.textView);
        Animation anim2 = AnimationUtils.loadAnimation(this,R.anim.activity_textanim);
        text.setAnimation(anim2);

        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 3000);
        // 3초 뒤 메인화면 진입
    }

    private class splashHandler implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));        // LoginActivity 로 이동
            finish();  // SplashActivity 종료
        }
    }
}