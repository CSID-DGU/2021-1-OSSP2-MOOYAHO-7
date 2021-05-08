package com.example.mooyaho;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mooyaho.adapter.CustomAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button buttonHome;
    Button buttonRequest;
    Button buttonChatting;
    Button buttonMyPage;

    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    private  String BASE_URL = "http://10.0.2.2:3000";
    List<PostResult> rs;

    private ArrayList<PostResult> mArrayList;
    private CustomAdapter mAdapter;
    private int count = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();                             // 보기 편하게, view bind 함수 생성
        setButtonClickListener();
        handleGetAll();
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

    public void recycleTest(){ // 리사이클러 뷰 만들기
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mArrayList = new ArrayList<>();

        mAdapter = new CustomAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        for(int i=0;i<rs.size();i++){
            count++;

            PostResult data = new PostResult(
                    count+ rs.get(count).getPostTitle(),
                    "" + rs.get(count).getPostContent(),
                    "" + count);

            //mArrayList.add(0, dict); //RecyclerView의 첫 줄에 삽입
            mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        buttonHome = (Button) findViewById(R.id.home);
        buttonRequest = (Button) findViewById(R.id.request);
        buttonChatting = (Button) findViewById(R.id.chatting);
        buttonMyPage = (Button) findViewById(R.id.mypage);

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
                startActivity(new Intent(getApplicationContext(), ChattingActivity.class));
            }
        });
        buttonMyPage.setOnClickListener(new View.OnClickListener() { // 마이페이지 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
            }
        });
    }
}