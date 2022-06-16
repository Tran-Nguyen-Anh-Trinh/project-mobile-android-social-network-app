package com.midterm.cloneinstagram.PushNotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.midterm.cloneinstagram.Controller.Activity.ChatActivity;
import com.midterm.cloneinstagram.Controller.Activity.HomeChatActivity;
import com.midterm.cloneinstagram.Controller.Activity.MainActivity;
import com.midterm.cloneinstagram.R;

import java.util.Map;
import java.util.Random;

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

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
                intent.putExtra("from", "notify");
            }
        } else {
            title = remoteMessage.getNotification().getTitle();
            text = remoteMessage.getNotification().getBody();
            intent = new Intent(this, MainActivity.class);
        }
        CHANNEL_ID = "MESSAGES";
        TYPE = "Messages Notification";
        ID = 0;
        LEVEL = NotificationManager.IMPORTANCE_HIGH;

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    TYPE,
                    LEVEL);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.hashtag_100px)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setAutoCancel(true);

            stackBuilder.addNextIntent(intent);
            PendingIntent notificationIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                notificationIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                notificationIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            };
            notification.setContentIntent(notificationIntent);
            Random generator = new Random();
            NotificationManagerCompat.from(this).notify(generator.nextInt(10000), notification.build());
            super.onMessageReceived(remoteMessage);
        }
    }
}