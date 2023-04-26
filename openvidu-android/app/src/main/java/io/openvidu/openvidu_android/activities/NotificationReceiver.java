package io.openvidu.openvidu_android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //String sessionid = intent.getStringExtra("Noti_session_id");
        String sessionida = intent.getStringExtra("data");
        Toast.makeText(context, "onrcive by intet ->>" + sessionida, Toast.LENGTH_LONG).show();



  /*
        Intent customintent = new Intent("anil_activity");
        customintent.putExtra("SESSION_ID", sessionida);
        customintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(customintent);
*/


        Intent intentone = new Intent(context.getApplicationContext(), SessionActivity.class);

        intentone.putExtra("SESSION_ID", sessionida);
        intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentone);


        Toast.makeText(context, "onrcive close ->>", Toast.LENGTH_LONG).show();


    }
}
