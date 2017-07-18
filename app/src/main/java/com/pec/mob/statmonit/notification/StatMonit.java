package com.pec.mob.statmonit.notification;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class StatMonit extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
