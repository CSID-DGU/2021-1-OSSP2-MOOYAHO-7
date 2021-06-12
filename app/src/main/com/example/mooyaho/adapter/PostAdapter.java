package com.example.mooyaho.adapter;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooyaho.DeliverRequestActivity;
import com.example.mooyaho.FoldingCell;
import com.example.mooyaho.MainActivity;
import com.example.mooyaho.ProfileActivity;
import com.example.mooyaho.R;
import com.example.mooyaho.data_class.PostResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {


    StorageReference storageReference;
    String postID;
    private Context mContext;
    private ArrayList<PostResult> mListPost;
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

        storageReference = FirebaseStorage.getInstance().getReference(); // storage 정보

        holder.tvPostTitle.setText(post.getPostTitle());
        //holder.tvPostContnet.setText(post.getPostContent());
        holder.tvPostTitle2.setText(post.getPostTitle());
        holder.tvPostContent2.setText(post.getPostContent());
        holder.tvUser.setText(post.getUserEmail());
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
            }

        });

        holder.buttonProfile.setOnClickListener(new View.OnClickListener() {
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
        private TextView tvPostTitle2;
        private TextView tvPostContent2;
        private ImageView postImage;
        private Button buttonProfile;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            foldingCell = itemView.findViewById(R.id.folding_cell);
            tvPostTitle = itemView.findViewById(R.id.tv_title_post);
            tvPostContnet = itemView.findViewById(R.id.tv_content_post);

            tvUser = itemView.findViewById(R.id.tv_user);
            tvPostTitle2 = itemView.findViewById(R.id.tv_title_post2);
            tvPostContent2 = itemView.findViewById(R.id.tv_content_post2);
            postImage = itemView.findViewById(R.id.post_image);

            buttonProfile = itemView.findViewById(R.id.profile_button);
        }
    }
}
