package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mooyaho.data_class.PostResult;
import com.example.mooyaho.data_class.Review;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewTestActivity extends AppCompatActivity {
    private TextView receiver;
    private TextView sender;
    private TextView content;
    private String rate;
    private Button submit;
    // HTTP 통신을 위한 라이브러리
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    private  String BASE_URL = "http://123.214.18.194:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";
    // 사용자가 정의한 통신 방법? RESTFUL API? 그런 느낌
    private RetrofitInterface retrofitInterface;

    List<Review> rs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_test);

        // Spinner
        Spinner rateSpinner = (Spinner)findViewById(R.id.review_rate);
        ArrayAdapter rateAdapter = ArrayAdapter.createFromResource(this,
                R.array.rate, android.R.layout.simple_spinner_item);
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rateSpinner.setAdapter(rateAdapter);

        rate = rateSpinner.getSelectedItem().toString(); // rate 값을 콤보박스에서 가져옴.
        // end Spinner

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        receiver = findViewById(R.id.review_receiver);
        sender = findViewById(R.id.review_sender);
        content = findViewById(R.id.review_content);
        submit = findViewById(R.id.review_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleReview();
            }
        });

    }

    public void handleGetReview(){
        String reviewSender = sender.getText().toString();
        String reviewReceiver = receiver.getText().toString();
        String reviewContent = content.getText().toString();
        String reviewRate = rate;
        String reviewDate = "210612";
        HashMap<String, String> map = new HashMap<>();
        map.put("reviewReceiver", reviewReceiver);

        Call<List<Review>> call = retrofitInterface.executeGetReview(map); // retrofit으로 get 실행
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                rs = response.body(); // response.body에는 모든 요청 객체가 배열로 담겨져 있음
                for(int i=0;i<rs.size();i++){
                    Toast.makeText(ReviewTestActivity.this, rs.get(i).getReviewDate() , Toast.LENGTH_LONG).show();

                    Log.e("Response", rs.get(i).getReviewContent());
                }

            }

            @Override
            public void onFailure(Call<List<Review>>  call, Throwable t) { // 디비에 글 없음(글 0개 최초 경우)
                //Toast.makeText(DeliverRequestActivity.this, "정보 가져오기 실패 2", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void handleReview(){


        String reviewSender = sender.getText().toString();
        String reviewReceiver = receiver.getText().toString();
        String reviewContent = content.getText().toString();
        String reviewRate = rate;
        String reviewDate = "210612";
        HashMap<String, String> map = new HashMap<>();
        map.put("reviewSender", reviewSender);
        map.put("reviewReceiver", reviewReceiver);
        map.put("reviewContent", reviewContent);
        map.put("reviewRate", reviewRate);
        map.put("reviewDate", reviewDate);

        Call<Void> call = retrofitInterface.executeReview(map); // retrofit으로 post 실행
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { // 대답 옴
                if(response.code() == 200){ // 글 작성 성공(서버에서 200 보내줌)
                    Toast.makeText(ReviewTestActivity.this, "글 작성 성공", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{ // 서버에서 200 안보내줌
                    Toast.makeText(ReviewTestActivity.this, "글 작성 실패 1", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) { // 대답 자체가 안옴(서버 안열었거나..)
                Toast.makeText( ReviewTestActivity.this, "글 작성 실패 2", Toast.LENGTH_LONG).show();
            }
        });

    }
}