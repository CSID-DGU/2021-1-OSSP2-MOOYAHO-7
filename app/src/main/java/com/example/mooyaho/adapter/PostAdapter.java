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

    // HTTP ????????? ?????? ???????????????
    private Retrofit retrofit;
    // ????????? IP ?????? = BASE_URL : ??????????????? ?????? ??? ?????? IP ??????
    private  String BASE_URL = "http://123.214.18.194:3000";
    // ?????????????????? ?????? ???(?????? ????????? ???????????? ?????? ???)
    //private  String BASE_URL = "http://10.0.2.2:3000";
    // ???????????? ????????? ?????? ??????? RESTFUL API? ?????? ??????
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
        storageReference = FirebaseStorage.getInstance().getReference(); // storage ??????

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
        //Log.d("?????????",post.getUserEmail());
        postID = post.getPostID();

        // users ?????? ?????? userID+profile.jpg ????????? ???????????? ??????
        StorageReference fileRef = storageReference.child("posts/"+postID+"_postImage.jpg"); // ????????? ??????
        //Log.d("??????", "posts/"+postID+"_postImage.jpg");

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override // ????????? ?????? ?????? ??? ???????????? picasso??? ????????? profileImage??? ??????
            public void onSuccess(Uri uri) {
                //Log.d("??????", "????????? ???????????? ??????");
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
                // intent ??????????????? userEmail????????? ???????????? ?????? ?????? ????????? ??? ??????????????? ?????? ?????????.
                mContext.startActivity(intent2);
            }
        });

        holder.tvUser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(mContext.getApplicationContext(), ProfileActivity.class);
                intent2.putExtra("userEmail",  post.getUserEmail());
                // intent ??????????????? userEmail????????? ???????????? ?????? ?????? ????????? ??? ??????????????? ?????? ?????????.
                mContext.startActivity(intent2);
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("???????????? ????????? ????????? ????????? ??? ????????????."); //??????
                dlg.setMessage("????????? ?????????????????????????"); // ?????????
                dlg.setIcon(R.drawable.logo); // ????????? ??????

                dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
//                ?????? ????????? ??????
                dlg.setNegativeButton("??????",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // ?????? ??????

                        String postEmail = post.getUserEmail().trim();
                        String userEmail = user.getEmail().trim();


                        if(postEmail.equals(userEmail)) {
                            //????????? ?????????

                            HashMap<String, String> map = new HashMap<>(); // HashMap ????????? request ??????
                            // HashMap??? ?????? ??????
                            map.put("postID", post.getPostID());
                            Call<Void> call = retrofitInterface.executeDelete(map); // retrofit?????? post ??????
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) { // ?????? ???
                                    if(response.code() == 200){ // ??? ?????? ??????(???????????? 200 ?????????)
                                        Intent intent2 = new Intent(mContext.getApplicationContext(), MainActivity.class);
                                        mContext.startActivity(intent2);
                                    }
                                    else{ // ???????????? 200 ????????????
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) { // ?????? ????????? ??????(?????? ???????????????..)
                                    Toast.makeText( view.getContext() , "??? ?????? ?????? 2", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        else{
                            Toast.makeText( view.getContext() , "??? ???????????? ????????????!!", Toast.LENGTH_LONG).show();


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
                                Toast.makeText(view.getContext(), "????????? ????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();

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
