package com.example.mooyaho.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooyaho.R;
import com.example.mooyaho.RetrofitInterface;
import com.example.mooyaho.ReviewTestActivity;
import com.example.mooyaho.data_class.PostResult;
import com.example.mooyaho.data_class.Review;
import com.example.mooyaho.data_class.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>{

    // HTTP 통신을 위한 라이브러리
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    private  String BASE_URL = "http://123.214.18.194:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";
    // 사용자가 정의한 통신 방법? RESTFUL API? 그런 느낌
    private RetrofitInterface retrofitInterface;
    private String userID;
    StorageReference storageReference;
    String postID;
    String nickname = "";

    private Context mContext;
    private ArrayList<Review> mListReview;
    public ReviewAdapter(Context mContext){
        this.mContext = mContext;
    }
    public void setData(ArrayList<Review> list){
        mListReview = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = 400; //height recycleviewer
        holder.itemView.setLayoutParams(params);


        Review review = mListReview.get(position);

        if(review == null){
            return;
        }

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보


        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("email").equalTo(review.getReviewSender());


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String receiverEmail = "";

                    for(DataSnapshot snap : snapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        nickname = user.getNickname();
                    }

                    holder.reviewName.setText(nickname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.reviewDate.setText(review.getReviewDate());

        double rate = Double.parseDouble(review.getReviewRate());
        String star = "";
        for(int i=0;i<rate;i++){
            star += "★";
        }

        int r = 5 - star.length();
        for(int i=0;i<r;i++){
            star += "☆";
        }

        holder.reviewRate.setText(star);
        holder.reviewContent.setText(review.getReviewContent());

        Query query2 = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("email").equalTo(review.getReviewSender());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        //Toast.makeText(ProfileActivity.this, user.getUid() , Toast.LENGTH_LONG).show();
                        // 설정
                        userID = user.getUid();
                    }
                    StorageReference profileRef = storageReference.child("users/"+userID+"profile.jpg");
                    if(profileRef != null) {
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override // 이미지 다운 성공 시 오픈소스 picasso를 사용해 profileImage에 설정
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.reviewProfile);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {

        if(mListReview == null)
            return 0;
        else{
            return mListReview.size();
        }
    }

    public class ReviewHolder extends RecyclerView.ViewHolder{

        private ImageView reviewProfile;
        private TextView reviewName;
        private TextView reviewContent;
        private TextView reviewDate;
        private TextView reviewRate;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            reviewProfile = itemView.findViewById(R.id.review_profile);
            reviewName = itemView.findViewById(R.id.review_name);
            reviewContent = itemView.findViewById(R.id.review_content);
            reviewDate = itemView.findViewById(R.id.review_date);
            reviewRate = itemView.findViewById(R.id.review_rate);
        }
    }

}