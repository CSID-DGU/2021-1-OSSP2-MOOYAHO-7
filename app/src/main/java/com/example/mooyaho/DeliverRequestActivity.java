package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeliverRequestActivity extends AppCompatActivity {

    List<PostResult> postArray;
    EditText titleEditText;
    EditText startEditText;
    EditText endEditText;
    EditText contentEditTxt;
    Button buttonSubmit;
    Button buttonGet;
    Button buttonfindLoc;
    ImageView buttonMap;
    // HTTP 통신을 위한 라이브러리
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    //private  String BASE_URL = "http://192.168.78.193:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    private  String BASE_URL = "http://10.0.2.2:3000";
    // 사용자가 정의한 통신 방법? RESTFUL API? 그런 느낌
    private RetrofitInterface retrofitInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_request);
        initView();
        setButtonClickListener();
    }

    private void initView() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        titleEditText = (EditText) findViewById(R.id.title);
        startEditText = (EditText) findViewById(R.id.startLocation);
        endEditText = (EditText) findViewById(R.id.endLocation);
        contentEditTxt = (EditText) findViewById(R.id.content);
        buttonSubmit = (Button) findViewById(R.id.submit);
        buttonGet = (Button) findViewById(R.id.get);
        buttonMap = (ImageView) findViewById(R.id.mapbutton);
        buttonfindLoc = (Button) findViewById(R.id.find_loc2);
    }

    private void setButtonClickListener() {
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePost();
            }
        });

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleGet();
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShowMapActivity.class));
            }
        });

        buttonfindLoc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(), FindMapActivity.class));
            }
        });

    }

    private void handlePost() { // 요청 post시 실행

        HashMap<String, String> map = new HashMap<>(); // HashMap 형태로 request 보냄
        // HashMap에 정보 넣기
        map.put("postTitle", titleEditText.getText().toString());
        map.put("postContent", contentEditTxt.getText().toString());
        map.put("startLatitude", "시작 위도 정보");
        map.put("startLongitude", "시작 경도 정보");
        map.put("endLatitude", "끝 위도 정보");
        map.put("endLongitude", "끝 경도 정보");


        Call<Void> call = retrofitInterface.executePost(map); // retrofit으로 post 실행
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { // 대답 옴
                if(response.code() == 200){ // 글 작성 성공(서버에서 200 보내줌)
                    Toast.makeText(DeliverRequestActivity.this, "글 작성 성공", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else{ // 서버에서 200 안보내줌
                    Toast.makeText(DeliverRequestActivity.this, "글 작성 실패 1", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) { // 대답 자체가 안옴(서버 안열었거나..)
                Toast.makeText( DeliverRequestActivity.this, "글 작성 실패 2", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleGet() { // 서버에서 정보 가져오기
        Call<PostResult> call = retrofitInterface.executeGet(); // retrofit으로 get 실행
        call.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) { // 서버에서 대답 옴

                Log.d("body", response.body().toString());
                if(response.code() == 200) { // 정보 가져오기 성공(서버에서 200 보내줌)
                    PostResult postResult = response.body(); // response의 body를 PostResult 형태로 받음
                    // 이제 PostResult의 get으로 액티비티 내용을 채움
                    titleEditText.setText(postResult.getPostTitle());
                    contentEditTxt.setText(postResult.getPostContent());
                }
                else{
                    Toast.makeText(DeliverRequestActivity.this, "정보 가져오기 실패 1", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.e("erroR", t.toString());
                Toast.makeText(DeliverRequestActivity.this, "정보 가져오기 실패 2", Toast.LENGTH_LONG).show();
            }
        });
    }


}