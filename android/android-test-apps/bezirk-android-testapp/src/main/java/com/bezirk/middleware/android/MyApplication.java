package com.bezirk.middleware.android;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BezirkMiddleware.initialize(this);
        Log.d("MyApplication", "Application is starting");
    }
}
