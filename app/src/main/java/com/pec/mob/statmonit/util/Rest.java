package com.pec.mob.statmonit.util;


import android.util.Base64;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Rest {

    private static final String TAG = Rest.class.getSimpleName();
    private static String username;// = "";
    private static String password;// = "";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void setCredential(String email, String pswd) {
        username = email;
        password = pswd;
    }

    public static String get(String req, String username, String password) {
        try {
            Log.d(TAG,req);
            OkHttpClient client = new OkHttpClient();
            String auth = username+":"+password;
            String val = "Basic " + Base64.encodeToString(auth.getBytes(),Base64.URL_SAFE | Base64.NO_WRAP);
            Request request = new Request.Builder()
                    .url(req)
                    .addHeader("Authorization",val)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            return "cannot retrieve data from server";
        }
    }

    public static String get(String req) {
        try {
            Log.d(TAG,req);
            System.out.println(req);
            OkHttpClient client = new OkHttpClient();
            String auth = username+":"+password;
            String val = "Basic " + Base64.encodeToString(auth.getBytes(),Base64.URL_SAFE | Base64.NO_WRAP);
            Request request = new Request.Builder()
                    .url(req)
                    .addHeader("Authorization",val)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            return "cannot retrieve data from server";
        }
    }

    public static String post(String url, String json) {
        try {
            Log.d(TAG,url+json);
            //System.out.println(url);
            OkHttpClient client = new OkHttpClient();
            String auth = username+":"+password;
            String val = "Basic " + Base64.encodeToString(auth.getBytes(),Base64.URL_SAFE | Base64.NO_WRAP);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization",val)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            return "cannot retrieve data from server";
        }
    }
}
