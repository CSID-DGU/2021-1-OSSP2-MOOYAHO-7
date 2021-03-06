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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mooyaho.adapter.PostAdapter;
import com.example.mooyaho.adapter.ReviewAdapter;
import com.example.mooyaho.data_class.PostResult;
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

public class MyPageActivity extends AppCompatActivity {

    ImageButton buttonHome;
    ImageButton buttonRequest;
    ImageButton buttonChatting;
    ImageButton buttonMyPage;

    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String userID;
    Button buttonLogout;
    ImageView profileImage;
    Button buttonUpload;
    String userEmail;
    TextView tvScore;
    RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;


    RecyclerView myRequestRecycler;
    PostAdapter postAdapter;

    List<Review> rs;
    List<PostResult> prs;

    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    // ????????? IP ?????? = BASE_URL : ??????????????? ?????? ??? ?????? IP ??????
    // ????????? ????????? ????????? 3 Ipv4 ??????
    private  String BASE_URL = "http://123.214.18.194:3000";
    //private  String BASE_URL = "http://192.168.115.193:3000";
    // ?????????????????? ?????? ???(?????? ????????? ???????????? ?????? ???)
    //private  String BASE_URL = "http://10.0.2.2:3000";

    DatabaseReference reviewReference;
    TextView review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        initView();
        setButtonClickListener();

    }
    private void initView() {
        buttonHome = (ImageButton) findViewById(R.id.home);
        buttonRequest = (ImageButton) findViewById(R.id.request);
        buttonChatting = (ImageButton) findViewById(R.id.chatting);
        buttonMyPage = (ImageButton) findViewById(R.id.mypage);

        tvScore = (TextView)findViewById(R.id.score);
        profileImage = (ImageView)findViewById(R.id.profileImage);
        buttonLogout = (Button)findViewById(R.id.logout);
        buttonUpload = (Button)findViewById(R.id.upload);

        // retrofit
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        // Firebase ??????
        user = FirebaseAuth.getInstance().getCurrentUser(); // ?????? ??????
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Users ?????? DB ??????

        storageReference = FirebaseStorage.getInstance().getReference(); // storage ??????
        userID = user.getUid(); // ?????? ????????? ????????? id


        final TextView emailTextView = (TextView)findViewById(R.id.emailAddress);
        final TextView nicknameTextView = (TextView)findViewById(R.id.nickname);

        // Users Database?????? ?????? ??????[?????????, ????????? ...] ???????????? ??????
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // ?????? ???
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){ // ?????? ????????? ?????????

                    // String ????????????
                    String nickname = userProfile.nickname;
                    String email = userProfile.email;
                    userEmail = userProfile.email;
                    // ??????
                    nicknameTextView.setText(nickname);
                    emailTextView.setText(email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { // ?????? ???

                Toast.makeText(MyPageActivity.this, "Something gone wrong !!",Toast.LENGTH_LONG).show();

            }
        });



        downloadImage();
        handleGetReview();
        handleGetRequest();

    }

    private void recycleRequest(){
        myRequestRecycler = findViewById(R.id.my_request);
        myRequestRecycler.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestRecycler.setLayoutManager(linearLayoutManager);
        myRequestRecycler.setFocusable(false);

        postAdapter = new PostAdapter(this);
        myRequestRecycler.setAdapter(postAdapter);
        postAdapter.setData(getDataPost());

        RecyclerDecoration spaceDecoration = new RecyclerDecoration(5);
        myRequestRecycler.addItemDecoration(spaceDecoration);
    }

    public ArrayList<PostResult> getDataPost(){
        ArrayList<PostResult> list = new ArrayList<>();

        for(int i=0;i<prs.size();i++){
            PostResult newPost
                    = new PostResult(prs.get(i).getPostID(),
                    prs.get(i).getUserEmail(),
                    prs.get(i).getPostTitle(),
                    prs.get(i).getPostContent(),
                    prs.get(i).getPostStartLatitude(),
                    prs.get(i).getPostStartLongitude(),
                    prs.get(i).getPostEndLatitude(),
                    prs.get(i).getPostEndLongitude()
            );
            list.add(newPost);
            //Log.e("rs", rs.get(i).getReviewContent());
        }
        return list;
    }

    private void handleGetRequest(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // ?????? ?????? ??? ????????? ??????
                HashMap<String, String> map = new HashMap<>();
                map.put("userEmail", user.getEmail());
                //Log.e("rs", user.getEmail());
                Call<List<PostResult>> call = retrofitInterface.executeGetRequest(map); // getAll??? ????????? ??????
                call.enqueue(new Callback<List<PostResult>>() {
                    @Override
                    public void onResponse(Call<List<PostResult>> call, Response<List<PostResult>> response) {
                        prs = response.body();

                        //Log.e("rs", prs.get(0).getPostContent());

                        recycleRequest();
                    }

                    @Override
                    public void onFailure(Call<List<PostResult>> call, Throwable t) {

                    }
                });

            }
        }, 500); // 0.5??????
    }

    private void handleGetReview(){
        ArrayList<Double> scores = new ArrayList<Double>();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // ?????? ?????? ??? ????????? ??????
                HashMap<String, String> map = new HashMap<>();
                map.put("reviewReceiver", userEmail);
                Call<List<Review>> call = retrofitInterface.executeGetReview(map); // getAll??? ????????? ??????
                call.enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        Handler mHandler2 = new Handler();
                        mHandler2.postDelayed(new Runnable()  {
                            public void run() {
                                // ?????? ?????? ??? ????????? ??????
                                rs = response.body(); // response.body?????? ?????? ?????? ????????? ????????? ????????? ??????
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
                                recycleTest(); // ?????? ?????? ???????????? recycler view ?????????

                            }
                        }, 0); // 0.5??????
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        //Log.e("Size", "Failed");
                    }
                });
            }
        }, 500); // 0.5??????
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
                    rs.get(i).getReviewRate() // ????????? loaction
            );
            list.add(newReview);
            //Log.e("rs", rs.get(i).getReviewContent());
        }
        return list;
    }



    private void downloadImage(){

        // users ?????? ?????? userID+profile.jpg ????????? ???????????? ??????
        StorageReference profileRef = storageReference.child("users/"+userID+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override // ????????? ?????? ?????? ??? ???????????? picasso??? ????????? profileImage??? ??????
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


    }

    private void setButtonClickListener() {
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // ???????????? ??????


             FirebaseAuth.getInstance().signOut();
             startActivity(new Intent(MyPageActivity.this, LoginActivity.class));
             finish();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // ????????? ????????? ??????
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() { // ?????????
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // ?????? ????????? DeliverRequestActivity ??? ??????
                startActivity(new Intent(getApplicationContext(), DeliverRequestActivity.class));
            }
        });
        buttonChatting.setOnClickListener(new View.OnClickListener() { // ????????? ?????? ??????
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChatList.class));
            }
        });
        buttonMyPage.setOnClickListener(new View.OnClickListener() { // ??????????????? ?????? ??????
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // ????????? ??????????????? ????????? ???????????????
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) { // ???????????? ????????? ??????????????? ???????????? ??? ???????????? ????????????????????? ????????????
        StorageReference fileRef = storageReference.child("users/"+userID+"profile.jpg"); // ????????? ??????
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // ????????? ??????
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // ?????? ??? ???????????????
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    } // ???????????? ??????
                });
                Toast.makeText(MyPageActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show(); // ??????
            }
        }).addOnFailureListener(new OnFailureListener() { // ??????
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyPageActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}