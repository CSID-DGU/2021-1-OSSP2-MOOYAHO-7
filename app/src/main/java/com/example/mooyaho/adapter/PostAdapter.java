package com.example.mooyaho.adapter;

import android.content.Context;
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
import com.example.mooyaho.ShowMapFragment;
import com.example.mooyaho.data_class.PostResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {


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

        private TextView PriceEditText;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            foldingCell = itemView.findViewById(R.id.folding_cell);
            tvPostTitle = itemView.findViewById(R.id.tv_title_post);
            tvPostContnet = itemView.findViewById(R.id.tv_content_post);
            PriceEditText = itemView.findViewById(R.id.price);
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

            geocoder = new Geocoder(itemView.getContext(), Locale.KOREA);
        }
    }
}
