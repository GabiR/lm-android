package com.cypien.leroy.utils;/*
 * Created by Alex on 26.09.2016.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegistrationIntentService extends FirebaseInstanceIdService {

    private static final String TAG = "RegistrationService";

    @Override
    public void onTokenRefresh() {


        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        // TODO: send token to your app server.
    }
}