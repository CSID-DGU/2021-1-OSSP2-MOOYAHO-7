package com.example.mooyaho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity { // 비밀번호 재설정

    EditText editTextEmail;
    Button buttonReset;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
        setButtonClickListener();
    }
    private void initView() {
        editTextEmail = (EditText)findViewById(R.id.email);
        buttonReset = (Button)findViewById(R.id.reset);
        auth = FirebaseAuth.getInstance();
    }

    private void setButtonClickListener() {
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() { // 패스워드 재설정
        // 이메일 string 가져오기
        String email = editTextEmail.getText().toString().trim();
       // string 검사
        if(email.isEmpty()){ // 입력 안됨
            editTextEmail.setError("이메일을 입력하세요!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ // 입력 패턴 이상함
            editTextEmail.setError("올바른 형태의 이메일을 입력하세요!");
            editTextEmail.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() { // 재설정 이메일 보냄
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){ // 성공 시
                    Toast.makeText(ResetPasswordActivity.this, "이메일 발송됨", Toast.LENGTH_LONG).show();
                }
                else{ // 실패 시 [이메일 없거나 등등..]
                    Toast.makeText(ResetPasswordActivity.this, "이메일 발송 실패", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}