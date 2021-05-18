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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mooyaho.data_class.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MyPageActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String userID;
    Button buttonLogout;
    ImageView profileImage;
    Button buttonUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        initView();
        setButtonClickListener();

    }
    private void initView() {
        profileImage = (ImageView)findViewById(R.id.profileImage);
        buttonLogout = (Button)findViewById(R.id.logout);
        buttonUpload = (Button)findViewById(R.id.upload);
        final TextView emailTextView = (TextView)findViewById(R.id.emailAddress);
        final TextView nicknameTextView = (TextView)findViewById(R.id.nickname);

        // Firebase 관련
        user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 정보
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Users 안의 DB 정보
        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보
        userID = user.getUid(); // 현재 유저의 고유한 id


        // users 폴더 안에 userID+profile.jpg 사진을 다운로드 받음
        StorageReference profileRef = storageReference.child("users/"+userID+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override // 이미지 다운 성공 시 오픈소스 picasso를 사용해 profileImage에 설정
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        // Users Database에서 유저 정보[이메일, 닉네임 ...] 가져오는 과정
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // 성공 시
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){ // 유저 정보가 있으면

                    // String 가져와서
                    String nickname = userProfile.nickname;
                    String email = userProfile.email;
                    // 설정
                    nicknameTextView.setText(nickname);
                    emailTextView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { // 실패 시
                Toast.makeText(MyPageActivity.this, "Something gone Wrong!!",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setButtonClickListener() {
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 로그아웃 버튼
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MyPageActivity.this, LoginActivity.class));
                finish();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 이미지 업로드 버튼
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
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
                Toast.makeText(MyPageActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show(); // 성공
            }
        }).addOnFailureListener(new OnFailureListener() { // 실패
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyPageActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}