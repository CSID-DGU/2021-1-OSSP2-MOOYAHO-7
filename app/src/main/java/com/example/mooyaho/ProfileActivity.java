package com.example.mooyaho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mooyaho.adapter.ReviewAdapter;
import com.example.mooyaho.data_class.Review;
import com.example.mooyaho.data_class.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* ProfileActivity 호출하는 코드 :

Intent intent2 = new Intent(getApplicationContext(), ProfileActivity.class);
intent2.putExtra("userEmail", "colamango@dgu.edu");
// intent 넘어가면서 userEmail이라는 이름으로 해당 유저 이메일 값 보내주기만 하면 됩니다.
startActivity(intent2);
 */


public class ProfileActivity extends AppCompatActivity {

    ImageButton buttonHome;
    ImageButton buttonRequest;
    ImageButton buttonChatting;
    ImageButton buttonMyPage;
    Button logout;

    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String userID;
    ImageView profileImage;
    String userEmail, nickname, email;
    ArrayList<User> userProfile = new ArrayList<User>();

    TextView emailTextView;
    TextView nicknameTextView;

    TextView tvScore;


    RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    List<Review> rs;

    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    // 이더넷 어댑터 이더넷 3 Ipv4 주소
    private  String BASE_URL = "http://123.214.18.194:3000";
    //private  String BASE_URL = "http://192.168.115.193:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        setButtonClickListener();
        showProfile();
        handleGetReview();
    }

    private void handleGetReview(){
        ArrayList<Double> scores = new ArrayList<Double>();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // 시간 지난 후 실행할 코딩
                HashMap<String, String> map = new HashMap<>();
                map.put("reviewReceiver", userEmail);
                Call<List<Review>> call = retrofitInterface.executeGetReview(map); // getAll로 서버와 통신
                call.enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        Handler mHandler2 = new Handler();
                        mHandler2.postDelayed(new Runnable()  {
                            public void run() {
                                // 시간 지난 후 실행할 코딩
                                rs = response.body(); // response.body에는 모든 요청 객체가 배열로 담겨져 있음
                                for(int i=0;i<rs.size();i++){
                                    Double s = Double.parseDouble(rs.get(i).getReviewRate());
                                    scores.add(s);
                                }

                                double temp = 0;
                                for(int i=0;i<scores.size();i++){
                                    temp += scores.get(i);
                                }
                                temp /= scores.size();
                                if(scores.size() == 0){
                                    tvScore.setText("-");
                                }
                                else {
                                    String score = String.valueOf(temp).toString().substring(0, 3);
                                    tvScore.setText(score);
                                }
                                recycleTest(); // 이제 받은 내용으로 recycler view 만들기

                            }
                        }, 0); // 0.5초후
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        Log.e("Size", "Failed");
                    }
                });
            }
        }, 500); // 0.5초후
    }

    private void recycleTest(){
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);


        reviewAdapter = new ReviewAdapter(this);
        recyclerView.setAdapter(reviewAdapter);
        reviewAdapter.setData(getDataReview());

        RecyclerDecoration spaceDecoration = new RecyclerDecoration(5);
        recyclerView.addItemDecoration(spaceDecoration);

    }

    private ArrayList<Review> getDataReview(){
        ArrayList<Review> list = new ArrayList<>();

        for(int i=0;i<rs.size();i++){
            Review newReview
                    = new Review(
                    rs.get(i).getReviewSender(),
                    rs.get(i).getReviewReceiver(),
                    rs.get(i).getReviewContent(),
                    rs.get(i).getReviewDate(),
                    rs.get(i).getReviewRate() // 원래는 loaction
            );
            list.add(newReview);
            //Log.e("rs", rs.get(i).getReviewContent());
        }
        return list;
    }

    private void showProfile() {

        Intent myIntent = getIntent();
        userEmail = myIntent.getStringExtra("userEmail");
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("email").equalTo(userEmail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        //Toast.makeText(ProfileActivity.this, user.getUid() , Toast.LENGTH_LONG).show();
                        userID = user.getUid();
                        nickname = user.getNickname();
                        email = user.getEmail();
                        // 설정
                        nicknameTextView.setText(nickname);
                        emailTextView.setText(email);
                    }

                    StorageReference profileRef = storageReference.child("users/"+userID+"profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override // 이미지 다운 성공 시 오픈소스 picasso를 사용해 profileImage에 설정
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profileImage);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initView() {

        logout = (Button) findViewById(R.id.logout);
        buttonHome = (ImageButton) findViewById(R.id.home);
        buttonRequest = (ImageButton) findViewById(R.id.request);
        buttonChatting = (ImageButton) findViewById(R.id.chatting);
        buttonMyPage = (ImageButton) findViewById(R.id.mypage);

        tvScore = (TextView)findViewById(R.id.score);

        profileImage = (ImageView)findViewById(R.id.profileImage);
        emailTextView = (TextView)findViewById(R.id.emailAddress);
        nicknameTextView = (TextView)findViewById(R.id.nickname);
        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        // Firebase 관련
        user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 정보
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Users 안의 DB 정보

        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보
        userID = user.getUid(); // 현재 유저의 고유한 id


    }

    private void setButtonClickListener() {

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
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
                //startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
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

    private void uploadImageToFirebase(Uri imageUri) { // 이미지를 사용자 갤러리에서 가져오는 데 성공하면 파이어베이스에 업로드함
        StorageReference fileRef = storageReference.child("users/"+userID+"profile.jpg"); // 업로드 위치
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // 업로드 성공
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // 그걸 또 다운받아서
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    } // 이미지로 설정
                });
                Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show(); // 성공
            }
        }).addOnFailureListener(new OnFailureListener() { // 실패
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}