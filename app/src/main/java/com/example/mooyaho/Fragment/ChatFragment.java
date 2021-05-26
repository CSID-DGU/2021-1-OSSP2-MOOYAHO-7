package com.example.mooyaho.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooyaho.R;
import com.example.mooyaho.chat.MessageActivity;
import com.example.mooyaho.data_class.Chat;
import com.example.mooyaho.data_class.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatFragment extends Fragment {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return view;
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<Chat> chatlist= new ArrayList<>();
        String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot item : snapshot.getChildren()){
                        chatlist.add(item.getValue(Chat.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;

            //일일이 채팅방에 있는 유저 체크
            for(String user: chatlist.get(position).users.keySet()){
                if(!user.equals(uid)){
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            FirebaseDatabase.getInstance().getReference().child("Users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    customViewHolder.textView_title.setText(user.nickname); //바인딩
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //메세지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            Map<String, Chat.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatlist.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            customViewHolder.textView_last_message.setText(chatlist.get(position).comments.get(lastMessageKey).message); //바인딩

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class); //방 열어주기 위함
                    intent.putExtra("destinationUid", destinationUsers.get(position));

                    startActivity(intent);
                }
            });

            //timestamp
            long unixTime = (long) chatlist.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            customViewHolder.textView_timestamp.setText(time);
        }

        @Override
        public int getItemCount() {
            return chatlist.size();


        }


        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;
            public CustomViewHolder(View view) {
                super(view);

                textView_title =  (TextView)view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = (TextView)view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp =  (TextView)view.findViewById(R.id.chatitem_textview_timestamp);
            }
        }
    }
}
