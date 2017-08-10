package com.pec.mob.statmonit.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.util.Rest;

import java.io.IOException;

public class RegistrationService extends IntentService {
    public RegistrationService() {
        super("RegistrationService");
    }
    private static final String TAG = RegistrationService.class.getSimpleName();
    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());

        try {
            String registrationToken = instanceID.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );
            boolean res = sendToken(registrationToken);
            if(res) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("RegisteredNotification", true);
                editor.putString("token", registrationToken);
                editor.commit();
            }
            //GcmPubSub subscription = GcmPubSub.getInstance(this);
            //subscription.subscribe(registrationToken, "/topics/my_little_topic", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean sendToken(String token) {
        try {
            String url = "https://report.pec.ir:444/api/acc/registerUserToken?ostype=1&token=" + token;
            Log.d(TAG,"=============>"+token+"<==============");
            String response = Rest.get(url);
            boolean b = Boolean.parseBoolean(response.trim());
            if (b) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return false;
        }
    }
}
