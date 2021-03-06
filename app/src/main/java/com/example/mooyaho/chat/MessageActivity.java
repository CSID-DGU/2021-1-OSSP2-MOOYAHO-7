package com.example.mooyaho.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mooyaho.MainActivity;
import com.example.mooyaho.R;
import com.example.mooyaho.RetrofitInterface;
import com.example.mooyaho.RetrofitTossInterface;
import com.example.mooyaho.ReviewTestActivity;
import com.example.mooyaho.data_class.Chat;
import com.example.mooyaho.data_class.PostResult;
import com.example.mooyaho.data_class.PostTossResult;
import com.example.mooyaho.data_class.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageActivity extends AppCompatActivity {

    private String receiver = "";
    private String sender = "";

    private String result = "";
    private String destinationUid;
    private Button button;
    private ImageView addButton;
    private EditText editText;

    private String uid;
    private Uri profileUri;
    private String chatRoomUid;
    private RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();         // firebase cloud functions
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference(); // storage ??????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //????????? ???????????? uid, ???????????? ???????????? uid
        destinationUid = getIntent().getStringExtra("destinationUid"); //????????? ????????? uid
        button = (Button) findViewById(R.id.messageActivity_button);
        addButton = (ImageView) findViewById(R.id.addButton);
        editText = (EditText) findViewById(R.id.messageActivity_editText);
        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);
        getProfileUri(destinationUid);


        //setAddButtonListener();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"?????? ?????? ??????", "?????? ????????????"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);

                builder.setTitle("??????")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_LONG).show();
                                if(which == 0)
                                    showcustomDialog();
                                else if(which == 1){

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Query query = FirebaseDatabase.getInstance().getReference("Users")
                                            .orderByChild("uid").equalTo(destinationUid);

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                String receiverEmail = "";

                                                for(DataSnapshot snap : snapshot.getChildren()){
                                                    User user = snap.getValue(User.class);
                                                    receiver = user.getEmail();
                                                }

                                                Intent intent3 = new Intent(v.getContext(), ReviewTestActivity.class);
                                                intent3.putExtra("sender", user.getEmail().toString());
                                                intent3.putExtra("receiver", receiver.toString());

                                                startActivity(intent3);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Chat chat = new Chat();
                chat.users.put(uid, true);
                chat.users.put(destinationUid, true);

                if (chatRoomUid == null) { //null?????? ????????? ??????
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });

                } else { //null??? ????????? ???????????? ????????? ?????? ?????? ????????? ??????
                    Chat.Comment comment = new Chat.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("chatrooms")
                            .child(chatRoomUid)
                            .child("comments")
                            .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

                        // DB??? ????????? ?????? ?????? ???
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editText.setText("");
                            callCloudFunction(comment);         // ???????????? ?????? ???????????? ????????? notification ?????? ??????
                        }
                    });
                }
            }
        });

        checkChatRoom();

    }

    private void showcustomDialog() {
        final String[] bank = new String[1];

        final AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);

        builder.setTitle("????????????")
                .setMessage("?????? ?????? ????????? ???????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    // ?????? ?????? ?????????
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog d = (Dialog) dialog;
                        Spinner spinner = (Spinner) d.findViewById(R.id.spinner_field);
                        EditText bankAccountNo = (EditText) d.findViewById(R.id.bankAccountNo);
                        EditText amount = (EditText) d.findViewById(R.id.amount);

                        String accountNo = bankAccountNo.getText().toString();
                        String moneyAmount = amount.getText().toString();

                        // Toss Link ??????
                        if (!accountNo.isEmpty() && !moneyAmount.isEmpty() && spinner.getSelectedItemPosition() != 0) {
                            moneyAmount = moneyAmount.replace(",", "");
                            Toast.makeText(getApplicationContext(), accountNo + " " + moneyAmount + " " + bank[0], Toast.LENGTH_SHORT).show();
                            requestTosslink(bank, accountNo, moneyAmount);

                        } else {
                            Toast.makeText(getApplicationContext(), "????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        View mView = getLayoutInflater().inflate(R.layout.edit_text_dialog, null);

        Spinner spinner = (Spinner) mView.findViewById(R.id.spinner_field);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(MessageActivity.this, R.array.spinnerArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItemPosition() > 0) {
                    //????????? ??????
                    Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    bank[0] = (String) spinner.getSelectedItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Edit text decimal format ??????
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        EditText amount = (EditText) mView.findViewById(R.id.amount);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",","")));
                    amount.setText(result);
                    amount.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        amount.addTextChangedListener(watcher);

        builder.setView(mView);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void requestTosslink(String[] bankName, String bankAccount, String amount) {
        Retrofit retrofit;
        String BASE_URL = "https://toss.im/transfer-web/linkgen-api/";


        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        HashMap<String, String> map = new HashMap<>(); // HashMap ????????? request ??????

        // HashMap??? ?????? ??????
        map.put("apiKey", "4c4fcaf5cc1840f1b9aa60da5df23f0c");
        map.put("bankName", bankName[0]);
        map.put("bankAccountNo", bankAccount);
        map.put("amount", amount);
        map.put("message", "?????? ??????");

        RetrofitTossInterface retrofitInterface = retrofit.create(RetrofitTossInterface.class);
        Call<PostTossResult> call = retrofitInterface.executePost(map); // retrofit?????? post ??????

        call.enqueue(new Callback<PostTossResult>() {
            @Override
            public void onResponse(Call<PostTossResult> call, Response<PostTossResult> response) { // ?????? ???
                Log.d("Response Code", String.valueOf(response.code()));

                // ?????? ?????? ???
                if (response.code() == 200) {
                    PostTossResult result = response.body();
                    String link = result.getSuccess().getLink();
                    Log.d("response", link);


                    // ????????? ??????
                    Chat.Comment comment = new Chat.Comment();
                    comment.uid = uid;
                    comment.message = link;
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("chatrooms")
                            .child(chatRoomUid)
                            .child("comments")
                            .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

                        // DB??? ????????? ?????? ?????? ???
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });

                }
                // ?????? ?????????
                else {
                    Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostTossResult> call, Throwable t) { // ?????? ????????? ??????(?????? ???????????????..)
                Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ???????????? ????????? URI??? ???????????? ??????
    private void getProfileUri(String userID) {
        // users ?????? ?????? userID+profile.jpg ????????? ???????????? ??????
        StorageReference profileRef = storageReference.child("users/" + userID + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override // ????????? ?????? ?????? ??? ???????????? picasso??? ????????? profileImage??? ??????
            public void onSuccess(Uri uri) {
                profileUri = uri;
            }
        });
    }

    //????????? ?????? ?????? ??????
    void checkChatRoom() {

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Chat chat = item.getValue(Chat.class);
                    if (chat.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey(); //??? ?????????
                        button.setEnabled(true);
                        //recyclerView??? ?????????
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        //????????? ??????
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ?????????????????? ???????????? ?????? Http Request
    private Task<String> callCloudFunction(Chat.Comment comment) {
        Map<String, Object> data = new HashMap<>();             // ????????? ????????? ?????? Map
        data.put("comment", comment.message);
        data.put("receiverUid", destinationUid);

        return mFunctions.getHttpsCallable("notiRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Map<String, Object> resultData = (Map<String, Object>) task.getResult().getData();
                        String result = (String) resultData.get("result");
                        Log.d("Cloud Functions : ", result);                    // response ?????? ?????? ??????
                        return result;
                    }
                });
    }

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Chat.Comment> comments;
    User user;

    public RecyclerViewAdapter() {
        comments = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                getMessageList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getMessageList() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    comments.add(item.getValue(Chat.Comment.class));
                }
                //????????? ??????
                notifyDataSetChanged();
                recyclerView.scrollToPosition((comments.size() - 1));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //xml??? ????????? view ?????????
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(view); //??? ??????????????? ?????? ?????????
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);
        if (comments.get(position).uid.equals(uid)) { //???????????? ?????????
            messageViewHolder.imageView_profile.setVisibility(View.GONE);
            String message = comments.get(position).message;
            messageViewHolder.textView_massage.setText(message);
            messageViewHolder.textView_massage.setBackgroundResource(R.drawable.right);
            messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
            messageViewHolder.textView_massage.setTextSize(25);
            messageViewHolder.linearLayout_messageContainer.setGravity(Gravity.RIGHT);
        } else { //???????????? ?????? ?????????
            messageViewHolder.imageView_profile.setVisibility(View.VISIBLE);
            messageViewHolder.textView_name.setText(user.nickname);
            String message = comments.get(position).message;
            messageViewHolder.textView_massage.setText(message);
            messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
            messageViewHolder.textView_massage.setBackgroundResource(R.drawable.left);
            messageViewHolder.textView_massage.setTextSize(25);
            messageViewHolder.linearLayout_messageContainer.setGravity(Gravity.LEFT);
            Picasso.get().load(profileUri).placeholder(R.drawable.ic_launcher_foreground).into(messageViewHolder.imageView_profile);
        }

        long unixTime = (long) comments.get(position).timestamp;
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = simpleDateFormat.format(date);
        messageViewHolder.textView_timestamp.setText(time);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

private class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView textView_massage;
    public TextView textView_name;
    public LinearLayout linearLayout_destination;
    public LinearLayout linearLayout_main;
    public LinearLayout linearLayout_messageContainer;
    public TextView textView_timestamp;
    public CircleImageView imageView_profile;

    public MessageViewHolder(View view) {
        super(view);
        textView_massage = (TextView) view.findViewById((R.id.messageItem_textView_message));
        textView_name = (TextView) view.findViewById((R.id.messageItem_textView_name));
        linearLayout_destination = (LinearLayout) view.findViewById((R.id.messageItem_linearLayout_destination));
        linearLayout_main = (LinearLayout) view.findViewById((R.id.messageItem_linearLayout_main));
        linearLayout_messageContainer = (LinearLayout) view.findViewById(R.id.messageContainer);
        textView_timestamp = (TextView) view.findViewById((R.id.messageItem_textView_timestamp));
        imageView_profile = (CircleImageView) view.findViewById(R.id.chatProfileImage);
    }
}
}