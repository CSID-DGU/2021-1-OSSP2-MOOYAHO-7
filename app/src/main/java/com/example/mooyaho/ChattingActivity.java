package com.example.mooyaho;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mooyaho.adapter.MessageRecyclerAdapter;
import com.example.mooyaho.data_class.Message;
import com.example.mooyaho.data_class.User;
import com.example.mooyaho.decoration.RecyclerDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ChattingActivity extends AppCompatActivity {
    // View
    RecyclerView recyclerView;
    MessageRecyclerAdapter adapter;
    EditText inputMessage;
    Button button;

    // Data
    ArrayList<Message> messages = new ArrayList<>();
    String userID;
    String nickname = User.nickname;
    String email = User.email;

    // Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 정보
    DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users"); // Users 안의 DB 정보
    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
            .child("ChatRooms")
            .child("room1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        initView();
        getUserData();              // userdata 가져오기
        setEnterPressedListener();
        setButtonClickListener();
        getMessages();              // messages 가져오기

    }

    private void initView() {
        // 어댑터와 리사이클러뷰 바인딩
        recyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);
        adapter = new MessageRecyclerAdapter(messages, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 리사이클러뷰 아이템 간격 설정
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(20);
        recyclerView.addItemDecoration(spaceDecoration);
        
        // 뷰 바인딩
        inputMessage = (EditText)findViewById(R.id.inputMessage);
        button = (Button)findViewById(R.id.sendButton);
    }

    private void setEnterPressedListener() {
        inputMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 메세지
                    String messageText = inputMessage.getText().toString();

                    // 시간
                    Calendar calendar = Calendar.getInstance();
                    String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

                    // DB 데이터 추가
                    Message message = new Message(nickname, messageText, time, email);
                    db.push().setValue(message);

                    inputMessage.setText("");
                    inputMessage.clearFocus();
                }
                else if(keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return true;
            }
        });
    }

    private void setButtonClickListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메세지
                String messageText = inputMessage.getText().toString();

                // 시간
                Calendar calendar = Calendar.getInstance();
                String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

                // DB 데이터 추가
                Message message = new Message(nickname, messageText, time, email);
                db.push().setValue(message);

                inputMessage.setText("");
                inputMessage.clearFocus();
            }
        });
    }

    private void getMessages() {
        Query query;

        query = FirebaseDatabase.getInstance().getReference()
                .child("ChatRooms")
                .child("room1");

        query.addChildEventListener(new ChildEventListener() {

            // DB에 메세지가 추가될때 마다 이 콜백 함수는 호출됩니다.
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class); // 메세지가 데이터베이스에 새로 추가 되었을 때 그 데이터를 자동으로 Message 클래스 형식에 맞게 가져와 객체를 생성한다.
                messages.add(message);
                adapter.notifyDataSetChanged();     // 변경 확정
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 필요 없음
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // 삭제 기능 만든다면 필요함
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 필요 없음
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "전송 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData() {
        // Users Database에서 유저 정보[이메일, 닉네임 ...] 가져오는 과정
        userID = user.getUid();
        userDB.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // 성공 시
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){ // 유저 정보가 있으면
                    // String 가져와서
                    nickname = userProfile.nickname;
                    email = userProfile.email;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { // 실패 시
                Toast.makeText(getApplicationContext(), "Something gone Wrong!!",Toast.LENGTH_LONG).show();
            }
        });
    }
}