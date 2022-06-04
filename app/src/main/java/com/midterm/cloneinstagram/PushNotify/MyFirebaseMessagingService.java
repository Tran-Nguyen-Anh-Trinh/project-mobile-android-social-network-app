package com.midterm.cloneinstagram.PushNotify;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.midterm.cloneinstagram.ChatActivity;
import com.midterm.cloneinstagram.MainActivity;
import com.midterm.cloneinstagram.R;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String title = "";
    String text = "";
    String idSend = "";
    String nameSend = "";
    String avaSend = "";
    String Uid = "";
    String Name = "";
    String ReceiverImg = "";
    String CHANNEL_ID = "";
    String TYPE = "";
    Intent intent;
    int ID = 1;
    int LEVEL = 0;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        if (!data.isEmpty()) {
            title = data.get("title").toString();
            text = data.get("body").toString();
            idSend = data.get("idSend").toString();
            nameSend = data.get("nameSend").toString();
            avaSend = data.get("avaSend").toString();
            Uid = data.get("Uid").toString();
            Name = data.get("Name").toString();
            ReceiverImg = data.get("ReceiverImg").toString();

            if(idSend.equals("")){
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("activity", "true");
            } else {
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("Uid", idSend);
                intent.putExtra("Name", nameSend);
                intent.putExtra("ReceiverImg", avaSend);
                intent.putExtra("idSend", Uid);
                intent.putExtra("nameSend", Name);
                intent.putExtra("avaSend", ReceiverImg);
            }
        } else {
            title = remoteMessage.getNotification().getTitle();
            text = remoteMessage.getNotification().getBody();
            intent = new Intent(this, MainActivity.class);
        }
        CHANNEL_ID = "MESSAGES";
        TYPE = "Messages Notification";
        ID = 1;
        LEVEL = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    TYPE,
                    LEVEL);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.hashtag_100px)
                    .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            PendingIntent notificationIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(notificationIntent);
            NotificationManagerCompat.from(this).notify(ID, notification.build());
            super.onMessageReceived(remoteMessage);
        }
    }
}