package com.example.mooyaho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button button;
    Button buttonLogin;
    EditText editTextEmail;
    EditText editTextPassword;
    TextView register;
    TextView reset;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        setButtonClickListener();
    }

    private void initView() {

        button = (Button) findViewById(R.id.button);
        buttonLogin = (Button) findViewById(R.id.login);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.register);
        reset = (TextView) findViewById(R.id.reset);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setButtonClickListener() {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() { // 회원 가입 창으로 이동
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() { // 로그인 실행
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() { // 비밀번호 리셋 창으로 이동
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
            }
        });
    }

    private void userLogin() { // 로그인을 담당하는 함수
        
        // 각각 string 받아오기
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        
        // 각각 string 조건 검사
        if(email.isEmpty()){ // 이메일 비어있음
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ // 이메일 이상함
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){ // 비밀번호 비어있음
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        
        // Auth 과정
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { // 인증 성공
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){ // 이메일 인증 됐을 시 메인액티비티로
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else{ // 아니면 안보내줌
                        Toast.makeText(LoginActivity.this, "이메일 인증 후 다시 눌러주세요",Toast.LENGTH_LONG).show();
                    }
                }
                else{ // 인증 실패
                    Toast.makeText(LoginActivity.this, "로그인 실패!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}