package com.example.mooyaho;

import com.example.mooyaho.data_class.PostResult;
import com.example.mooyaho.data_class.Review;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface { // 노드-mysql 연동을 위한 Retrofit 인터페이스

    @POST("/post") // /post로 갈 시 해당 함수를 실행한다
    Call<Void> executePost(@Body HashMap<String,String> map);

    @POST("/get") // /get으로 갈 시 해당 함수를 실행
    Call<PostResult> executeGet();

    @POST("/getAll")
    Call<List<PostResult>> getAll();

    @POST("/delete")
    Call<Void> executeDelete(@Body HashMap<String, String> map);

    @POST("/review")
    Call<Void> executeReview(@Body HashMap<String, String> map);

    @POST("/getReview")
    Call<List<Review>> executeGetReview(@Body HashMap<String, String> map);

}