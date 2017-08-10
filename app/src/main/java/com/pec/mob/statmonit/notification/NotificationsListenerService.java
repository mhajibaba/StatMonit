package com.pec.mob.statmonit.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.layout.LoginActivity;
import com.pec.mob.statmonit.layout.MainActivity;
import com.pec.mob.statmonit.layout.NotificationFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = "",body = "";
        long itemId=1;
        try {
            JSONObject jObj = new JSONObject(message);
            body = jObj.getString("Message");
            title = jObj.getString("Title");
            itemId = jObj.getLong("ItemId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent nIntent = new Intent(this.getApplicationContext(),MainActivity.class);
        nIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(this.getApplicationContext(), 0,
                nIntent, 0);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_alert)
                .setSound(Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.alert))
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(new long[]{1000,1000,1000,1000})
                .setContentIntent(intent);
        Notification n = mBuilder.build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify((int)itemId,n );
    }
}
