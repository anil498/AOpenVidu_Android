package io.openvidu.openvidu_android.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static io.openvidu.openvidu_android.activities.HomePage.channel_desc;
import static io.openvidu.openvidu_android.activities.HomePage.channel_id;
import static io.openvidu.openvidu_android.activities.HomePage.channel_name;

public class MyFirebaseMessagingServices extends FirebaseMessagingService {

    private String sessionIdfromNoti = "";

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        ///--------------------------------------------------
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channel_id,
//                    channel_name,
//                    NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription(channel_desc);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//        }

        ////////-------------------------------------------------
         // remoteMessage.getData().
        // super.onMessageReceived(remoteMessage);
        Log.d("anilcheck", "onmessageReecied called");
        Log.d("anilcheck", "message recieved from" + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("anilcheck", "onMessageReceived: Data: " + remoteMessage.getData().toString());


            // Extract the data from the remote message
            sessionIdfromNoti = remoteMessage.getData().get("SESSION_ID");  //by chatgpt


        }
        /////------------------------demo start -------------------------
        String titlee = remoteMessage.getData().get("TITLE");
        String bodyy = remoteMessage.getData().get("BODY");


        ///-----------for backpressd from notification
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(getApplicationContext(), SessionActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        resultIntent.putExtra("SESSION_ID", sessionIdfromNoti);
        resultIntent.putExtra("noti_id",1);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        ////----------------------------end backpressed


        ///for notification click
        Intent actintent = new Intent(getApplicationContext(), SessionActivity.class);
        actintent.putExtra("SESSION_ID", sessionIdfromNoti);
        actintent.putExtra("noti_id",1);
        actintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actpendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0,
                actintent,
                PendingIntent.FLAG_IMMUTABLE);


        //////


        //for btn click
        Intent broadcastintentt = new Intent(this, NotificationReceiver.class);

        broadcastintentt.putExtra("data", sessionIdfromNoti);


        PendingIntent pactionIntentt = PendingIntent.getBroadcast(this, 0, broadcastintentt, PendingIntent.FLAG_IMMUTABLE);

        //--------------------------

        NotificationCompat.Builder mbuilderr = new NotificationCompat.Builder(this,
                channel_id).setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentTitle(titlee)
                .setContentText(bodyy)
                .addAction(android.R.mipmap.sym_def_app_icon, "join call", actpendingIntent)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                  .setAutoCancel(true)
                .setPriority(PRIORITY_HIGH);
        NotificationManagerCompat m_notificationManagerCompatt =
                NotificationManagerCompat.from(this);

        m_notificationManagerCompatt.notify(1, mbuilderr.build());

        ///------------------------------end----------------------


        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            ///for activity
            //            Intent activityintent=new Intent(this,SessionActivity.class);
            //            PendingIntent contentintent=PendingIntent.getActivity(this,0,activityintent,PendingIntent.FLAG_IMMUTABLE);
            //            ////----
            Intent broadcastintent = new Intent(this, NotificationReceiver.class);

            broadcastintent.putExtra("data", sessionIdfromNoti);

            //sessionIdfromNoti="anilcustom";
            PendingIntent pactionIntent = PendingIntent.getBroadcast(this,
                    0,
                    broadcastintent,
                    PendingIntent.FLAG_IMMUTABLE);

            //            try {
            //                pactionIntent.send();
            //            } catch (PendingIntent.CanceledException e) {
            //                e.printStackTrace();
            //            }
            ///---


            // NotificationHelper.Display_Notification(getApplicationContext(),title,body);
            NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this,
                    channel_id).setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    .setContentTitle(title)
                    .setContentText(body)
                    .addAction(android.R.mipmap.sym_def_app_icon, "join call", pactionIntent)
                    .setAutoCancel(true)
                    //        .setContentIntent(contentintent)
                    .setPriority(PRIORITY_HIGH);
            NotificationManagerCompat m_notificationManagerCompat =
                    NotificationManagerCompat.from(this);


            m_notificationManagerCompat.notify(1, mbuilder.build());

        }//notification if close


    }
    //onMessageReceived close

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NotNull String token) {
        super.onNewToken(token);
    }


}//class close
