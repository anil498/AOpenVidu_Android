package io.openvidu.openvidu_android.Model;

public class Userdata {
    public String userphone;
    public String usertoken;

    public Userdata() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Userdata(String userphone, String usertoken) {
        this.userphone = userphone;
        this.usertoken = usertoken;
    }


}
