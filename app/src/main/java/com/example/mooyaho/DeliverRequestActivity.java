package com.example.mooyaho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.mooyaho.data_class.PostResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeliverRequestActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String userID;
    String postID;
    FirebaseUser user;

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
    //private  String BASE_URL = "http://192.168.115.193:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    private  String BASE_URL = "http://10.0.2.2:3000";
    // 사용자가 정의한 통신 방법? RESTFUL API? 그런 느낌
    private RetrofitInterface retrofitInterface;

    ImageView postImage;
    Button buttonUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_request);
        initView();
        setButtonClickListener();
        handleGet();
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

        buttonUpload = (Button) findViewById(R.id.upload);
        postImage = (ImageView) findViewById(R.id.postImage);

        user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 정보
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Users 안의 DB 정보
        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보
        userID = user.getUid(); // 현재 유저의 고유한 id

    }

    private void uploadImageToFirebase(Uri imageUri) { // 이미지를 사용자 갤러리에서 가져오는 데 성공하면 파이어베이스에 업로드
        // userID대신에 node에서  postID 받아서 +1 해서 업로드하고 싶음
        StorageReference fileRef = storageReference.child("posts/"+postID+"_postImage.jpg"); // 업로드 위치
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // 업로드 성공
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // 그걸 또 다운받아서
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(postImage);
                    } // 이미지로 설정
                });
                Toast.makeText(DeliverRequestActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show(); // 성공
            }
        }).addOnFailureListener(new OnFailureListener() { // 실패
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeliverRequestActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 사용자 갤러리에서 이미지 가져왔다면
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void setButtonClickListener() {

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 이미지 업로드 버튼
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });


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
        map.put("postID", postID);
        map.put("postTitle", titleEditText.getText().toString());
        map.put("postContent", contentEditTxt.getText().toString());
        map.put("userEmail", user.getEmail().toString());
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

                if(response.code() == 200) { // 정보 가져오기 성공(서버에서 200 보내줌)

                    // 이제 PostResult의 get으로 액티비티 내용을 채움
                    postID = (response.body().getPostID());
                    Toast.makeText(DeliverRequestActivity.this, postID, Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(DeliverRequestActivity.this, "정보 가져오기 실패 1", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) { // 디비에 글 없음(글 0개 최초 경우)
                //Toast.makeText(DeliverRequestActivity.this, "정보 가져오기 실패 2", Toast.LENGTH_LONG).show();
                postID = "1";
            }
        });
    }


}