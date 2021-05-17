package com.example.mooyaho.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mooyaho.R;
import com.example.mooyaho.chat.MessageActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String channelId = "channelId";
    final static String GROUP_KEY = "notificationGroup";

    NotificationManager notiManger;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d("Token", s);
        saveToken(s);
    }

    private void saveToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("tokenValue", token);
        editor.commit();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FireBaseMessaging", "From: " + remoteMessage.getFrom());

        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
        } else {
            Log.i("수신에러: ", "data가 비어있습니다. 메시지를 수신하지 못했습니다.");
            Log.i("data값: ", remoteMessage.getData().toString());
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        int uniId = (int) (System.currentTimeMillis() / 7);

        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임한다.
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity Stack 을 경로만 남긴다. A-B-C-D-B => A-B
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT);

        // 알림에 대한 UI 정보와 작업을 지정한다.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        NotificationCompat.Builder summary = new NotificationCompat.Builder(getApplicationContext(), channelId);

        builder.setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘 설정
                .setContentTitle(title) // 제목
                .setContentText(body) // 메시지 내용
                .setAutoCancel(true)   // 푸시 메시지 터치시 자동 삭제
                //.setSound(soundUri) // 알림 소리
                .setGroup(GROUP_KEY)
                .setContentIntent(pendingIntent); // 알림 실행 시 Intent

        summary.setContentTitle("알림")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setGroup(GROUP_KEY)
                .setAutoCancel(true)
                .setGroupSummary(true);    // 해당 알림이 Summary라는 설정

        notiManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //푸시울렸을때 화면깨우기.
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
        wakeLock.acquire(2000);

        // 알림 생성
        notiManger.notify(uniId, builder.build());
        notiManger.notify(0, summary.build());
    }
}

