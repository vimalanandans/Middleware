package com.bezirk.middleware.android;

import android.app.Application;
import android.util.Log;

import com.bezirk.middleware.core.proxy.Config;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Config config = new Config.ConfigBuilder().setLogLevel(Config.Level.DEBUG).setPackageLogLevel("com.bezirk.middleware.core.comms", Config.Level.ERROR).create();
        BezirkMiddleware.initialize(this, config);
  //      BezirkMiddleware.initialize(this);
        Log.d("MyApplication", "Application is starting");
    }
}
