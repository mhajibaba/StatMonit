package com.pec.mob.statmonit.util;

import android.os.AsyncTask;
import android.util.Base64;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RESTExecutor extends AsyncTask<String, Void, String> {

    private static String username;// = "reza@pec.ir";
    private static String password;// = "Aa123456.";

    public static void setCredential(String email, String pswd) {
        username = email;
        password = pswd;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            OkHttpClient client = new OkHttpClient();
            String auth = username+":"+password;
            String val = "Basic " + Base64.encodeToString(auth.getBytes(),Base64.URL_SAFE | Base64.NO_WRAP);
            Request request = new Request.Builder()
                    .url(params[0])
                    .addHeader("Authorization",val)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            return "cannot retrieve data from server";
        }
    }
}
