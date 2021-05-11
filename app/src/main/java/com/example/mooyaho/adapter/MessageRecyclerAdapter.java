package com.example.mooyaho.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooyaho.R;
import com.example.mooyaho.data_class.Message;
import com.example.mooyaho.data_class.User;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter {

    final int VIEW_TYPE_MY = 0;
    final int VIEW_TYPE_OTHER = 1;

    private ArrayList<Message> messages = new ArrayList<>();        // 채팅방에 있는 메세지들을 담을 배열
    private Context context;                                        // Adapter를 inner class로 작성 하지 않을 때는, context 객체를 Activity에서 받아와야함
    private String email;

    public MessageRecyclerAdapter(ArrayList<Message> messages, String email, Context context) {
        this.messages = messages;
        this.context = context;                                     // 생성자를 통해 Context 객체 할당
        this.email = email;
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.size() != 0) {
            Message message = messages.get(position);
            String email = message.getEmail();
            if(email != null && email.equals(this.email)) {     // 메세지의 이메일이 현재 유저의 이메일과 같으면 내 메세지로 취급한다.
                return VIEW_TYPE_MY;
            } else {
                return VIEW_TYPE_OTHER;
            }
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    // ViewHolder 객체와 message_item View 객체를 생성하는 함수
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 다른 사람 메세지
        if (viewType == VIEW_TYPE_OTHER) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);      // inflater 객체 생성
            View itemView = inflater.inflate(R.layout.other_message_item, parent, false);                       // inflater를 통해 message_item View 객체 생성
            return new MessageViewHolder(itemView);         // viewHolder 객체를 생성 후 리턴
        }
        // 내 메세지
        else if (viewType == VIEW_TYPE_MY) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);      // inflater 객체 생성
            View itemView = inflater.inflate(R.layout.my_message_item, parent, false);                       // inflater를 통해 message_item View 객체 생성
            return new MessageViewHolder(itemView);         // viewHolder 객체를 생성 후 리턴
        }
        // 도달 금지
        return null;
    }

    // ViewHolder 객체와 message 객체 데이터를 연결하는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);                       // 사용자의 리사이클러뷰 현재 위치(position)에 해당하는 Message 객체를 가져온다.
        MessageViewHolder viewHolder = (MessageViewHolder) holder;      // 뷰 홀더를 받아온다. (캐스팅 필요)
        viewHolder.setItem(message);                                    // 뷰 홀더에 Message 객체를 bind 한다.
    }

    @Override
    public int getItemCount() {
        return messages.size();         // messages 배열의 크기 = 현재 채팅방 리스트에 있는 메세지 수
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView chattingNickName, chattingMessage, chattingTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view){
            chattingNickName = (TextView)view.findViewById(R.id.chattingNickName);
            chattingMessage = (TextView)view.findViewById(R.id.chattingMessage);
            chattingTime = (TextView)view.findViewById(R.id.chattingTime);
        }

        public void setItem(Message message) {
            chattingNickName.setText(message.getNickname());        // Message 객체에서 닉네임 얻어오기
            chattingMessage.setText(message.getMessage());          // 메시지 얻어오기
            chattingTime.setText(message.getTime());        // 타임스탬프 얻어오기 (날짜 변환 구현 필요)
        }
    }
}
