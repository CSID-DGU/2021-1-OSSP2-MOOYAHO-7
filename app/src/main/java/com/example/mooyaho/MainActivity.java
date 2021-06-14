package com.example.mooyaho;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.mooyaho.adapter.PostAdapter;
import com.example.mooyaho.data_class.PostResult;
import com.google.firebase.database.annotations.NotNull;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageButton buttonHome;
    ImageButton buttonRequest;
    ImageButton buttonChatting;
    ImageButton buttonMyPage;
    ImageView buttonInfo;
    SwipeRefreshLayout swipeRefreshLayout;
    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    // 이더넷 어댑터 이더넷 3 Ipv4 주소
    private  String BASE_URL = "http://123.214.18.194:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    List<PostResult> rs;

    private NaverMap naverMap;

    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private double lat; // 현재 자신의 위치 위도
    private double lon; // 현재 자신의 위치 경도

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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        buttonHome = (ImageButton) findViewById(R.id.home);
        buttonRequest = (ImageButton) findViewById(R.id.request);
        buttonChatting = (ImageButton) findViewById(R.id.chatting);
        buttonMyPage = (ImageButton) findViewById(R.id.mypage);
        buttonInfo = (ImageView) findViewById(R.id.infobutton1);
        //buttonChatList = (Button) findViewById(R.id.ChatList);

        // retrofit
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void myLocationSearch(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NotNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        });
    }

    private void setButtonClickListener() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(false);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();


            }
        });

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
                startActivity(new Intent(getApplicationContext(), ChatList.class));
            }
        });
        buttonMyPage.setOnClickListener(new View.OnClickListener() { // 마이페이지 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
            }
        });
        buttonInfo.setOnClickListener(new View.OnClickListener() { // 지도 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShowInfoActivity.class));
            }
        });
/*        buttonChatList.setOnClickListener(new View.OnClickListener() { // chatlist 이동 버튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChatList.class));
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        myLocationSearch(naverMap);
    }
}