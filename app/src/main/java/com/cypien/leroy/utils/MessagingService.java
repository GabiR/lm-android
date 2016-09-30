package com.cypien.leroy.utils;/*
 * Created by Alex on 26.09.2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.cypien.leroy.R;
import com.cypien.leroy.activities.ShopDashboard;
import com.cypien.leroy.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.

        handleNotification(remoteMessage);
    }


    private void handleNotification(RemoteMessage remoteMessage) {
        // Create Notification

        Intent intent = new Intent(this, ShopDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notified", true);
        Message message = new Message();
        message.setContent(remoteMessage.getData().get("body"));
        message.setTitle(remoteMessage.getData().get("title"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        message.setDate(sdf.format(new Date(remoteMessage.getSentTime())));
        message.setId(remoteMessage.getMessageId());
        DatabaseConnector.getHelper(this).insertMessage(message);

        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}