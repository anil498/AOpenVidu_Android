package io.openvidu.openvidu_android.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.openvidu.openvidu_android.Model.Userdata;
import io.openvidu.openvidu_android.R;

public class HomePage extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST = 102;

    public static final String channel_id = "amCarbon_notification_id";
    //make public so directly acces by NotificationHelperr class
    public static final String channel_name = "amCarbon_notification_name";
    public static final String channel_desc = "amCarbon_notification_desc";
    public final String DEMO_SESSION_NAME = "anil_demo_session";
    private String session_id_from_noti = "";


//    @BindView(R.id.joinbtn)
//    Button call_join_btn;

    //    @BindView(R.id.pname)
    //    EditText puser_name_tv;

//    @BindView(R.id.roughtv)
//    EditText roughtvie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ButterKnife.bind(this);
          Log.d("anil","home page oncreate start");
        askForPermissions();
        //----------------
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            finish();
//            return;
//        }

        /////////////////----------for token------------


        //////--------------------------------
        //Question : hr baar channel create krne ki requirement hai kya
        ////////////ceate channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id,
                    channel_name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channel_desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        /////////////////////for leave


            ////------------get data from notifition pending intant
           if (getIntent() != null && getIntent().hasExtra("SESSION_ID")) {

                for (String key : getIntent().getExtras().keySet()) {
                    Log.d("anil",
                            "onCreate: Key: " + key + " Data: " +
                                    getIntent().getExtras().getString(key));
                    if (key.equals("SESSION_ID")) {
                        session_id_from_noti = getIntent().getExtras().getString(key);
                        //roughtvie.append(getIntent().getExtras().getString(key) + "\n");
                    }

                }

                if (!session_id_from_noti.equals("")) {
                    Intent intent = new Intent(getBaseContext(), SessionActivity.class);
                    intent.putExtra("SESSION_ID", session_id_from_noti);

                    //intent.putExtra("USER_NAME", "anildirectbck");

                    startActivity(intent);
                }

            }// --- if close for intent

        /////////--------------------------------------
        //        if (!session_id_from_noti.equals("")) {
        //            Intent intent = new Intent(getBaseContext(), SessionActivity.class);
        //            intent.putExtra("SESSION_ID", session_id_from_noti);
        //
        //            //intent.putExtra("USER_NAME", "anildirectbck");
        //
        //            startActivity(intent);
        //        }

    }/// on create close

   /* @Override
    protected void onPostResume() {
        super.onPostResume();
        Toast.makeText(this,"home activity  come in foreground sessionnotifi_id--->"+session_idfromnoti,Toast.LENGTH_LONG).show();
        if(!session_idfromnoti.equals("")) {
            Intent intent = new Intent(getBaseContext(), SessionActivity.class);
            intent.putExtra("SESSION_ID", session_idfromnoti);
            intent.putExtra("USER_NAME", "anildirectbck");

            startActivity(intent);
        }

    }*/
   public void askForPermissions() {
       if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
               PackageManager.PERMISSION_GRANTED) &&
               (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                       PackageManager.PERMISSION_GRANTED)) {
           ActivityCompat.requestPermissions(this,
                   new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                   MY_PERMISSIONS_REQUEST);
       } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
               PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,
                   new String[]{Manifest.permission.RECORD_AUDIO},
                   MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
       } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
               PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,
                   new String[]{Manifest.permission.CAMERA},
                   MY_PERMISSIONS_REQUEST_CAMERA);
       }
   }

//    private boolean arePermissionGranted() {
//        return (ContextCompat.checkSelfPermission(getApplicationContext(),
//                Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) &&
//                (ContextCompat.checkSelfPermission(getApplicationContext(),
//                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
//    }




    public void join_call(View view) {
        //String sname=s_name.getText().toString();
        //        String sname=DEMO_SESSION_NAME;

        // String pname= puser_name_tv.getText().toString();
        //Log.d("anil", "join_call: participant name is "+pname);

        Intent intent = new Intent(getBaseContext(), SessionActivity.class);
        intent.putExtra("SESSION_ID", DEMO_SESSION_NAME);
        //intent.putExtra("USER_NAME", pname);
        startActivity(intent);

    }

    public void registerPh(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        View dialoglay = getLayoutInflater().inflate(R.layout.takename_alert, null);
        EditText userPh_tv;
        userPh_tv = dialoglay.findViewById(R.id.printname);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enterno = userPh_tv.getText().toString();
                Toast.makeText(getApplicationContext(),
                        "user registration done by phone " + userPh_tv.getText().toString(),
                        Toast.LENGTH_LONG).show();
                GenerateToken(enterno);//call to generate token
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),
                        "Please Register your phone number",
                        Toast.LENGTH_LONG).show();

            }
        });

        builder.setView(dialoglay);

        builder.show();
    }

    private void GenerateToken(String userphone) {
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NotNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("check",
                                    "Fetching FCM registration token failed",
                                    task.getException());
                            return;
                        }
                        //note aur do override method aur hai nev token() ek aur ,etc

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("anilcheck", "token ->" + token);

                                //roughtvie.setText(token);
                        //savetoken(token);
                        StoreTokenINFirebase(token, userphone);

                    }

                });
    }//generate token close

    private void StoreTokenINFirebase(String token, String phoneno) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Userdata userdata = new Userdata(phoneno, token);
        db.collection("userdata")
                .document(phoneno)
                .set(userdata)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NotNull Task<Void> task) {
                        Toast.makeText(HomePage.this, "user registered in app", Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(HomePage.this,
                                "Error to store in firebase. Please Try again",
                                Toast.LENGTH_LONG).show();

                    }
                });

    }// close StoreTokenINFirebase(String token)


}//class clsoe