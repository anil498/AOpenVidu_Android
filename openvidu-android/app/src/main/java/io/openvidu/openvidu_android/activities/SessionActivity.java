package io.openvidu.openvidu_android.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.openvidu.openvidu_android.R;
import io.openvidu.openvidu_android.fragments.PermissionsDialogFragment;
import io.openvidu.openvidu_android.openvidu.LocalParticipant;
import io.openvidu.openvidu_android.openvidu.RemoteParticipant;
import io.openvidu.openvidu_android.openvidu.Session;
import io.openvidu.openvidu_android.utils.CustomHttpClient;
import io.openvidu.openvidu_android.websocket.CustomWebSocket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SessionActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST = 102;
    private final String TAG = "SessionActivity";
    @BindView(R.id.views_container)
    LinearLayout views_container;

    //    @BindView(R.id.start_finish_call)
    //    Button leave_btn;

    //    @BindView(R.id.button_leave_meeting)
    //    Button leave_btn;


    //    @BindView(R.id.session_name)
    //    EditText session_name;

    //    @BindView(R.id.participant_name)
    //    EditText participant_name;

    //    @BindView(R.id.application_server_url)
    //    EditText application_server_url;
    @BindView(R.id.local_gl_surface_view)
    SurfaceViewRenderer localVideoView;
    //    @BindView(R.id.main_participant)
    //    TextView main_participant;
    //    @BindView(R.id.peer_container)
    //    FrameLayout peer_container;

    private String APPLICATION_SERVER_URL = "https://demos.openvidu.io/";
    private Session session;
    private CustomHttpClient httpClient;
    private Boolean isvcopen = true;
    String sessionIdaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        sessionIdaa = getIntent().getStringExtra("SESSION_ID");
        String userName = getIntent().getStringExtra("USER_NAME");

        //---------------for cancle notifiation id
        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("noti_id", 1);
        NotificationManager manager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.cancel(intValue);
        //manager.cancel(tag, id);

        ///////-------------------
        if (sessionIdaa.equals("") || sessionIdaa == null) {
            sessionIdaa = "myCustomSession";

              //Toast.makeText(this, "sessionid by custom ->" + sessionIdaa, Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "sessionid of current session ->" + sessionIdaa, Toast.LENGTH_LONG).show();

        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        askForPermissions();
        ButterKnife.bind(this);
        Random random = new Random();
        int randomIndex = random.nextInt(100);
        // participant_name.setText(participant_name.getText().append(String.valueOf(randomIndex)));
       Toast.makeText(this,"sessionid of current session ->" + sessionIdaa,Toast.LENGTH_LONG).show();
        buttonPressed(sessionIdaa);
    }

    //    @Override
    //    protected void onPostResume() {
    //        super.onPostResume();
    //        Toast.makeText(this,"session activity  come in foreground",Toast.LENGTH_LONG).show();
    //    }

    public void askForPermissions() {

//        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATION) !=
//                PackageManager.PERMISSION_GRANTED) &&
//                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
//                        PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
//                    MY_PERMISSIONS_REQUEST);
//        }
         //for notifiation end
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
    /////--------------------


    ////-----------------------


    public void stopvc(View view) {

        if (isvcopen)//true to unpublish krna
        {
            ///-----------
            if (this.session != null) {
                this.session.leaveSession();
            }
            if (this.httpClient != null) {
                this.httpClient.dispose();
            }
            viewToDisconnectedState();

            ///////---

            //            if(this.session != null) {
            //                isvcopen=!isvcopen;
            //                Log.d("anil", "stopvc: run in sessionactivity");
            //                this.session.stopVideoCall();
            //
            //            }
            ///------------
        }
        if (!isvcopen)//false hai vc to publish krna
        {
            LocalParticipant localParticipant = new LocalParticipant("anildemo",
                    session,
                    this.getApplicationContext(),
                    localVideoView);
            localParticipant.startCamera();
        }

        return;
    }

    public void leaveMeetingPressed(View view) {
        leaveSession();
        //        //Since API 21 you can use a very similar command
        //        //finishAndRemoveTask();
        //        Intent backintent=new Intent(this,HomePage.class);
        //        backintent.putExtra("LEAVE", "leave");
        //         startActivity(backintent);
        //        finish();
        finish();
        System.exit(0);

    }

    //    public void buttonPressed(View view)
    public void buttonPressed(String sessionId) {
        // askForPermissions();

        //        if (start_finish_call.getText().equals(getResources().getString(R.string.hang_up))) {
        //            // Already connected to a session
        //            leaveSession();
        //            return;
        //        }
        try {
            arePermissionGranted();
        } catch (Exception e) {
            Toast.makeText(this, "error in arePermissionGranted(); call", Toast.LENGTH_LONG).show();
        }
        if (arePermissionGranted()) {
            try {
                initViews();
            } catch (Exception e) {
                Toast.makeText(this, "error in initViews(); call", Toast.LENGTH_LONG).show();
            }
            try {


                viewToConnectingState();
            } catch (Exception e) {
                Toast.makeText(this, "error in  viewToConnectingState(); call", Toast.LENGTH_LONG)
                        .show();
            }
            // APPLICATION_SERVER_URL = application_server_url.getText().toString();
            httpClient = new CustomHttpClient(APPLICATION_SERVER_URL);

            //String sessionId = session_name.getText().toString();
            try {


                getToken(sessionId);
            } catch (Exception e) {
                Toast.makeText(this, "error in  getToken(sessionId) call", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            DialogFragment permissionsFragment = new PermissionsDialogFragment();
            permissionsFragment.show(getSupportFragmentManager(), "Permissions Fragment");
        }
    }

    private void getToken(String sessionId) {
        try {
            // Session Request
            RequestBody sessionBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                            "{\"customSessionId\": \"" + sessionId + "\"}");
            this.httpClient.httpCall("/api/sessions",
                    "POST",
                    "application/json",
                    sessionBody,
                    new Callback() {

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response
                        ) throws IOException {
                            Log.d(TAG, "responseString: " + response.body().string());

                            // Token Request
                            RequestBody tokenBody = RequestBody.create(MediaType.parse(
                                    "application/json; charset=utf-8"), "{}");
                            httpClient.httpCall("/api/sessions/" + sessionId + "/connections",
                                    "POST",
                                    "application/json",
                                    tokenBody,
                                    new Callback() {

                                        @Override
                                        public void onResponse(@NotNull Call call,
                                                @NotNull Response response
                                        ) {
                                            String responseString = null;
                                            try {
                                                responseString = response.body().string();
                                            } catch (IOException e) {
                                                Log.e(TAG, "Error getting body", e);
                                            }
                                            getTokenSuccess(responseString, sessionId);
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call call,
                                                @NotNull IOException e
                                        ) {
                                            Log.e(TAG,
                                                    "Error POST /api/sessions/SESSION_ID/connections",
                                                    e);
                                            connectionError(APPLICATION_SERVER_URL);
                                        }
                                    });
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e(TAG, "Error POST /api/sessions", e);
                            connectionError(APPLICATION_SERVER_URL);
                        }
                    });
        } catch (IOException e) {
            Log.e(TAG, "Error getting token", e);
            e.printStackTrace();
            connectionError(APPLICATION_SERVER_URL);
        }
    }

    private void getTokenSuccess(String token, String sessionId) {
        // Initialize our session
        session = new Session(sessionId, token, views_container, this);

        // Initialize our local participant and start local camera
        // String participantName = participant_name.getText().toString();
        String participantName = "anilp";
        LocalParticipant localParticipant = new LocalParticipant(participantName,
                session,
                this.getApplicationContext(),
                localVideoView);
        localParticipant.startCamera();
        runOnUiThread(() -> {
            // Update local participant view
            // main_participant.setText(participant_name.getText().toString());
            //            main_participant.setText("anil");
            //
            //            main_participant.setPadding(20, 3, 20, 3);
            Log.d("anil", "gettoen success run");
        });

        // Initialize and connect the websocket to OpenVidu Server
        startWebSocket();
    }

    private void startWebSocket() {
        CustomWebSocket webSocket = new CustomWebSocket(session, this);
        webSocket.execute();
        session.setWebSocket(webSocket);
    }

    private void connectionError(String url) {

        //Toast.makeText(this, "connection error run", Toast.LENGTH_SHORT).show();
        Runnable myRunnable = () -> {
            Toast toast = Toast.makeText(this, "Error connecting to " + url, Toast.LENGTH_LONG);
            toast.show();
            viewToDisconnectedState();
        };
        new Handler(this.getMainLooper()).post(myRunnable);
    }

    private void initViews() {
        EglBase rootEglBase = EglBase.create();
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setMirror(true);
        localVideoView.setEnableHardwareScaler(true);
        localVideoView.setZOrderMediaOverlay(true);
    }

    public void viewToDisconnectedState() {
        //Toast.makeText(this, "viewToDisconnectedState() run", Toast.LENGTH_SHORT).show();
        runOnUiThread(() -> {
            localVideoView.clearImage();
            localVideoView.release();

            //            start_finish_call.setText(getResources().getString(R.string.start_button));
            //            start_finish_call.setEnabled(true);
            //            application_server_url.setEnabled(true);
            //            application_server_url.setFocusableInTouchMode(true);
            //            session_name.setEnabled(true);
            //            session_name.setFocusableInTouchMode(true);
            //            participant_name.setEnabled(true);
            //            participant_name.setFocusableInTouchMode(true);
            //            main_participant.setText(null);
            //            main_participant.setPadding(0, 0, 0, 0);

            //            finish();
            //            System.exit(0);
        });
    }

    public void viewToConnectingState() {
        runOnUiThread(() -> {
            //leave_btn.setEnabled(false);
            //            application_server_url.setEnabled(false);
            //            application_server_url.setFocusable(false);
            //            session_name.setEnabled(false);
            //            session_name.setFocusable(false);
            //            participant_name.setEnabled(false);
            //            participant_name.setFocusable(false);
        });
    }

    public void viewToConnectedState() {
        runOnUiThread(() -> {
            //            leave_btn.setText(getResources().getString(R.string.hang_up));
            //            leave_btn.setEnabled(true);
            Log.d("anil", "viewToConnectedState() start");
        });
    }

    public void createRemoteParticipantVideo(final RemoteParticipant remoteParticipant) {
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = () -> {
            View rowView = this.getLayoutInflater().inflate(R.layout.peer_video, null);
            //v,height
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
            lp.setMargins(16, 16, 16, 16);


            rowView.setLayoutParams(lp);
            int rowId = View.generateViewId();
            rowView.setId(rowId);
            views_container.addView(rowView);
            SurfaceViewRenderer videoView =
                    (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
            remoteParticipant.setVideoView(videoView);
            videoView.setMirror(false);
            EglBase rootEglBase = EglBase.create();
            videoView.init(rootEglBase.getEglBaseContext(), null);
            videoView.setZOrderMediaOverlay(true);
            View textView = ((ViewGroup) rowView).getChildAt(1);
            //remoteParticipant.setParticipantNameText((TextView) textView);
            remoteParticipant.setView(rowView);

            //  remoteParticipant.getParticipantNameText().setText(remoteParticipant.getParticipantName());
            // remoteParticipant.getParticipantNameText().setPadding(20, 3, 20, 3);
        };
        mainHandler.post(myRunnable);
    }

    public void setRemoteMediaStream(MediaStream stream, final RemoteParticipant remoteParticipant
    ) {
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        videoTrack.addSink(remoteParticipant.getVideoView());
        runOnUiThread(() -> {
            remoteParticipant.getVideoView().setVisibility(View.VISIBLE);
        });
    }

    public void leaveSession() {
        //Toast.makeText(this, "leavesession run", Toast.LENGTH_LONG).show();
        if (this.session != null) {
            this.session.leaveSession();
        }
        if (this.httpClient != null) {
            this.httpClient.dispose();
        }
        viewToDisconnectedState();
    }

    private boolean arePermissionGranted() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
    }

    @Override
    protected void onDestroy() {
        leaveSession();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        leaveSession();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        //leaveSession();
        super.onStop();
    }

}
