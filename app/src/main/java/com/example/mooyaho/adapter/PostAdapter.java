package com.example.mooyaho.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooyaho.DeliverRequestActivity;
import com.example.mooyaho.FoldingCell;
import com.example.mooyaho.MainActivity;
import com.example.mooyaho.ProfileActivity;
import com.example.mooyaho.R;
import com.example.mooyaho.RetrofitInterface;
import com.example.mooyaho.ShowMapFragment;
import com.example.mooyaho.chat.MessageActivity;
import com.example.mooyaho.data_class.PostResult;
import com.example.mooyaho.data_class.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // HTTP 통신을 위한 라이브러리
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    private  String BASE_URL = "http://123.214.18.194:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";
    // 사용자가 정의한 통신 방법? RESTFUL API? 그런 느낌
    private RetrofitInterface retrofitInterface;

    StorageReference storageReference;
    String postID;
    private Context mContext;
    private ArrayList<PostResult> mListPost;
    private Geocoder geocoder;

    public PostAdapter(Context mContext){
        this.mContext = mContext;
    }
    public void setData(ArrayList<PostResult> list){
        mListPost = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostResult post = mListPost.get(position);
        if(post == null){
            return;
        }
        double s_lat = Double.parseDouble(post.getPostStartLatitude());
        double s_lon = Double.parseDouble(post.getPostStartLongitude());

        double e_lat = Double.parseDouble(post.getPostEndLatitude());
        double e_lon = Double.parseDouble(post.getPostEndLongitude());

        String startLocation = getStartLocation(s_lat, s_lon);
        String endLocation = getEndLocation(e_lat, e_lon);

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보

        holder.tvPostTitle.setText(post.getPostTitle());
        //holder.tvPostContent.setText(post.getPostContent());
        holder.tvPostTitle2.setText(post.getPostTitle());
        holder.tvPostContent2.setText(post.getPostContent());
        holder.tvUser.setText(post.getUserEmail());
        holder.tvUser2.setText(post.getUserEmail());
        holder.start.setText(startLocation);
        holder.start2.setText(startLocation);
        holder.end.setText(endLocation);
        holder.end2.setText(endLocation);
        //Log.d("이메일",post.getUserEmail());
        postID = post.getPostID();

        // users 폴더 안에 userID+profile.jpg 사진을 다운로드 받음
        StorageReference fileRef = storageReference.child("posts/"+postID+"_postImage.jpg"); // 업로드 위치
        //Log.d("경로", "posts/"+postID+"_postImage.jpg");

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override // 이미지 다운 성공 시 오픈소스 picasso를 사용해 profileImage에 설정
            public void onSuccess(Uri uri) {
                //Log.d("성공", "이미지 다운로드 성공");
                Picasso.get().load(uri).into(holder.postImage);
                Picasso.get().load(uri).into(holder.postImage2);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        holder.tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(mContext.getApplicationContext(), ProfileActivity.class);
                intent2.putExtra("userEmail",  post.getUserEmail());
                // intent 넘어가면서 userEmail이라는 이름으로 해당 유저 이메일 값 보내주기만 하면 됩니다.
                mContext.startActivity(intent2);
            }
        });

        holder.tvUser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(mContext.getApplicationContext(), ProfileActivity.class);
                intent2.putExtra("userEmail",  post.getUserEmail());
                // intent 넘어가면서 userEmail이라는 이름으로 해당 유저 이메일 값 보내주기만 하면 됩니다.
                mContext.startActivity(intent2);
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("게사자가 자신이 아니면 삭제할 수 없습니다."); //제목
                dlg.setMessage("정말로 삭제하시겠습니까?"); // 메시지
                dlg.setIcon(R.drawable.logo); // 아이콘 설정

                dlg.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
//                버튼 클릭시 동작
                dlg.setNegativeButton("삭제",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 정보

                        String postEmail = post.getUserEmail().trim();
                        String userEmail = user.getEmail().trim();


                        if(postEmail.equals(userEmail)) {
                            //토스트 메시지

                            HashMap<String, String> map = new HashMap<>(); // HashMap 형태로 request 보냄
                            // HashMap에 정보 넣기
                            map.put("postID", post.getPostID());
                            Call<Void> call = retrofitInterface.executeDelete(map); // retrofit으로 post 실행
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) { // 대답 옴
                                    if(response.code() == 200){ // 글 작성 성공(서버에서 200 보내줌)
                                        Intent intent2 = new Intent(mContext.getApplicationContext(), MainActivity.class);
                                        mContext.startActivity(intent2);
                                    }
                                    else{ // 서버에서 200 안보내줌
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) { // 대답 자체가 안옴(서버 안열었거나..)
                                    Toast.makeText( view.getContext() , "글 작성 실패 2", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        else{
                            Toast.makeText( view.getContext() , "글 작성자가 아닙니다!!", Toast.LENGTH_LONG).show();


                        }


                    }
                });
                dlg.show();
            }

        });


        holder.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                Intent intent3 = new Intent(view.getContext(), MessageActivity.class);

                Query query = FirebaseDatabase.getInstance().getReference("Users")
                        .orderByChild("email").equalTo(post.getUserEmail());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String destinationUid = "";
                            
                            for(DataSnapshot snap : snapshot.getChildren()){
                                User user = snap.getValue(User.class);
                                destinationUid = user.getUid();
                            }

                            if(destinationUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Toast.makeText(view.getContext(), "자신의 요청은 수락할 수 없습니다.", Toast.LENGTH_LONG).show();

                            }
                            else {
                                intent3.putExtra("destinationUid", destinationUid);
                                mContext.startActivity(intent3);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


        holder.foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.foldingCell.toggle(false);
            }
        });
        holder.buttonConMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
                ShowMapFragment s = ShowMapFragment.getInstance(s_lat, s_lon, e_lat, e_lon);
                fm.beginTransaction().add(s,"show_map").commit() ;
            }
        });
    }

    private String getStartLocation(double lat, double lon) {

        List<Address> list = null;
        try {
            System.out.println("lat: " + lat + "lon: " + lon);
            list = geocoder.getFromLocation(lat, lon, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","my location error");
        }
        if(list != null) {
            if(list.size() != 0){
                return (list.get(0).getAddressLine(0));
            }
        }
        else {
            return "error";
        }
        return "error";
    }

    private String getEndLocation(double lat, double lon) {

        List<Address> list = null;
        try {
            System.out.println("lat: " + lat + "lon: " + lon);
            list = geocoder.getFromLocation(lat, lon, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","my location error");
        }
        if(list != null) {
            if(list.size() != 0){
                return (list.get(0).getAddressLine(0));
            }
        }
        else {
            return "error";
        }
        return "error";
    }

    @Override
    public int getItemCount() {
        if(mListPost == null)
            return 0;
        else{
            return mListPost.size();
        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        private FoldingCell foldingCell;
        private TextView tvPostTitle;
        private TextView tvPostContnet;
        private TextView tvUser;
        private TextView tvUser2;
        private TextView tvPostTitle2;
        private TextView tvPostContent2;
        private ImageView postImage;
        private ImageView postImage2;
        private Button buttonProfile;
        private Button buttonConMap;
        private TextView start;
        private TextView start2;
        private TextView end;
        private TextView end2;
        private Button buttonDelete;
        private Button postButton;

        private TextView PriceEditText;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postButton = itemView.findViewById(R.id.post_button);
            foldingCell = itemView.findViewById(R.id.folding_cell);
            tvPostTitle = itemView.findViewById(R.id.tv_title_post);
            tvPostContnet = itemView.findViewById(R.id.tv_content_post);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvUser2= itemView.findViewById(R.id.tv_user2);
            tvPostTitle2 = itemView.findViewById(R.id.tv_title_post2);
            tvPostContent2 = itemView.findViewById(R.id.tv_content_post2);
            postImage = itemView.findViewById(R.id.post_image);
            postImage2 = itemView.findViewById(R.id.post_image2);

            buttonConMap = itemView.findViewById(R.id.show_map_btn);

            start = itemView.findViewById(R.id.post_start);
            start2 = itemView.findViewById(R.id.post_start2);
            end = itemView.findViewById(R.id.post_end);
            end2 = itemView.findViewById(R.id.post_end2);

            buttonDelete = itemView.findViewById(R.id.delete_button);

            geocoder = new Geocoder(itemView.getContext(), Locale.KOREA);
        }
    }
}
