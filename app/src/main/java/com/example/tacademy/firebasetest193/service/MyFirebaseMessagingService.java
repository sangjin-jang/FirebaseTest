package com.example.tacademy.firebasetest193.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.tacademy.firebasetest193.MainActivity;
import com.example.tacademy.firebasetest193.R;
import com.example.tacademy.firebasetest193.model.ResPushModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by Tacademy on 2017-07-07.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    //메시지를 수신한다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {        //ctrl + i = implement, ctrl + o = override
        super.onMessageReceived(remoteMessage);

        Log.d("FCM", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            ResPushModel res = new Gson().fromJson(remoteMessage.getData().get("data"), ResPushModel.class);
            Log.d("FCM", "Message data payload: " + res.getBody());

            showNotification(res);
        }
    }

    //사용자에게 푸시 도착을 통보한다.(안테나 영역에 알림으로 처리)
    public void showNotification(ResPushModel res){

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(res.getTitle())
                .setContentText(res.getBody())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        //Notification 작동
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 0:Notification 고유 번호로 생각하고 눌러서 시작하면 해당 번호를 넣어서 알림을 삭제한다.
        notificationManager.notify(0, nb.build());
    }
}
