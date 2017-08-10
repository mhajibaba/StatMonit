package com.pec.mob.statmonit.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;

import java.net.InetAddress;

public class Network {

    public static boolean isNetworkConnected(Object systemService) {
        ConnectivityManager cm = (ConnectivityManager) systemService;

        return cm.getActiveNetworkInfo() != null;
    }


    public static boolean isInternetAvailable() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, e.getMessage());
            return false;
        }
    }
}
