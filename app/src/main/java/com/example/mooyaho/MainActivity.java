package com.example.mooyaho;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.mooyaho.adapter.PostAdapter;
import com.example.mooyaho.data_class.PostResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    ImageButton buttonHome;
    ImageButton buttonRequest;
    ImageButton buttonChatting;
    ImageButton buttonMyPage;
    ImageView buttonMap;

    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    // 이더넷 어댑터 이더넷 3 Ipv4 주소
    private  String BASE_URL = "http://10.90.0.110:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    List<PostResult> rs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get our folding cell
        /*final FoldingCell fc = (FoldingCell) findViewById(R.id.folding_cell);
        // attach click listener to fold btn
        final Button toggleBtn = (Button) findViewById(R.id.toggle_btn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });
        final Button toggleBtn2 = (Button) findViewById(R.id.toggle_btn6);
        toggleBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });*/
        initView();
        setButtonClickListener();
        handleGetAll();
    }

    private void recycleTest(){
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);

        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
        postAdapter.setData(getDataPost());

    }
    private ArrayList<PostResult> getDataPost(){
        ArrayList<PostResult> list = new ArrayList<>();
        for(int i=0;i<rs.size();i++) {
            PostResult newPost
                    = new PostResult(
                    rs.get(i).getPostID(),
                    rs.get(i).getUserEmail(),
                    rs.get(i).getPostTitle(),
                    rs.get(i).getPostContent(),
                    rs.get(i).getPostStartLatitude(),
                    rs.get(i).getPostStartLongitude(),
                    rs.get(i).getPostEndLatitude(),
                    rs.get(i).getPostEndLongitude()
            );
            list.add(newPost);
        }
        return list;
    }


    private void handleGetAll(){
        Call<List<PostResult>> call = retrofitInterface.getAll(); // getAll로 서버와 통신
        call.enqueue(new Callback<List<PostResult>>() {
            @Override
            public void onResponse(Call<List<PostResult>> call, Response<List<PostResult>> response) {
                rs = response.body(); // response.body에는 모든 요청 객체가 배열로 담겨져 있음
                recycleTest(); // 이제 받은 내용으로 recycler view 만들기
            }

            @Override
            public void onFailure(Call<List<PostResult>> call, Throwable t) {
            }
        });
    }

    private void initView() {
        buttonHome = (ImageButton) findViewById(R.id.home);
        buttonRequest = (ImageButton) findViewById(R.id.request);
        buttonChatting = (ImageButton) findViewById(R.id.chatting);
        buttonMyPage = (ImageButton) findViewById(R.id.mypage);
        buttonMap = (ImageView) findViewById(R.id.mapbutton1);
        //buttonChatList = (Button) findViewById(R.id.ChatList);

        // retrofit
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    private void setButtonClickListener() {
        buttonHome.setOnClickListener(new View.OnClickListener() { // 홈버튼
            @Override
            public void onClick(View v) {
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
                startActivity(new Intent(getApplicationContext(), ChatList.class));
            }
        });
        buttonMyPage.setOnClickListener(new View.OnClickListener() { // 마이페이지 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
            }
        });
        buttonMap.setOnClickListener(new View.OnClickListener() { // 지도 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShowMapActivity.class));
            }
        });
/*        buttonChatList.setOnClickListener(new View.OnClickListener() { // chatlist 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChatList.class));
            }
        });*/
    }
}