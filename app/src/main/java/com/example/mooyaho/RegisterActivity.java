package com.example.mooyaho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mooyaho.data_class.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button buttonRegister;
    EditText editTextEmail, editTextNickname, editTextPassword, editTextConfirm;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setButtonClickListener();
    }

    private void initView() {
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextNickname = (EditText) findViewById(R.id.nickname);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonRegister = (Button) findViewById(R.id.register);
        editTextConfirm = (EditText) findViewById(R.id.confirm);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setButtonClickListener() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() { // 회원가입을 수행하는 함수

        // 각각의 스트링들 받아서 trim 시킵니다
        final String email = editTextEmail.getText().toString().trim();
        final String nickname = editTextNickname.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm = editTextConfirm.getText().toString().trim();


        // 각각의 스트링 조건 검사
        if (nickname.isEmpty()) {
            editTextNickname.setError("Full name is required!");
            editTextNickname.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        int comma = email.indexOf('.');
        if(!email.substring(comma).equals(".edu")){
            editTextEmail.setError("Please provide valid mail!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid mail!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Valid Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)){
            editTextPassword.setError("비밀번호가 일치하지 않습니다.");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password) // email, password 로 회원가입
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) { // 회원가입 성공 시
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            User newUser = new User(email, nickname); // 새로운 유저 정보 클래스
                            FirebaseDatabase.getInstance().getReference("Users") // DB에 INSERT
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) { // DB INSERT 성공
                                        Toast.makeText(RegisterActivity.this, "회원가입이 성공적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (!user.isEmailVerified()) { // 이메일 인증이 안됐을 때 (처음은 무조건)
                                            user.sendEmailVerification(); // 이메일 링크 보냄
                                            Toast.makeText(RegisterActivity.this, "이메일 인증 후, 로그인 해주세요", Toast.LENGTH_SHORT).show();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                                }
                                            }, 2000);// 2.0초 정도 딜레이를 준 로그인 화면으로 보냄
                                        }
                                    } else { // DB INSERT 실패
                                        Toast.makeText(RegisterActivity.this, "회원가입 실패!", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        } else { // 회원가입 실패, 대부분 email이 이미 있을 때
                            Log.e("Error", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "이미 있는 계정입니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }



}