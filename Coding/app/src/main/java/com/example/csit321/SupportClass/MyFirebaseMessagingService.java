package com.example.csit321.SupportClass;

import android.app.Notification;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.example.csit321.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.logging.Handler;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //User receive notification

    private static final String TAG = "MyFirebaseMessagingService";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        final String CHANNEL_ID = "MESSAGE";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.scsg)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, notification.build());

        super.onMessageReceived(remoteMessage);
    }

//    @Override
//    public void onNewToken(String s)
//    {
//        super.onNewToken(s);
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String refreshToken = FirebaseMessaging.getInstance().getToken().getResult();
//        if (firebaseUser != null)
//        {
//            updateToken(refreshToken);
//        }
//    }
//
//    private void updateToken(String token)
//    {
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String userId= firebaseUser.getUid();
//        HashMap<String, Object> updateMap = new HashMap<>();
//        updateMap.put("token", token);
//        FirebaseFirestore.getInstance().collection("Tokens").document(userId).update(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (!task.isSuccessful())
//                {
//                    Log.d(TAG, "Update Token Failed");
//                }
//            }
//        });
//    }
}
