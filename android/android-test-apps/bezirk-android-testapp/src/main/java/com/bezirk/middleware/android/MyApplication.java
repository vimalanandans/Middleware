package com.bezirk.middleware.android;

import android.app.Application;
import android.util.Log;

import com.bezirk.middleware.core.proxy.Config;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();
        configBuilder.setLogLevel(Config.Level.DEBUG); //root log level
        //configBuilder.setPackageLogLevel("com.bezirk.middleware.core.comms", Config.Level.INFO); //package level logging
        configBuilder.setAppName("bezirk-android-testapp");
        //configBuilder.setComms(false); //disabling comms
        //configBuilder.setGroupName("Test Group"); //custom group
        //configBuilder.setServiceAlive(true); //to keep service alive even after app shutdown

        BezirkMiddleware.initialize(this, configBuilder.create());
        //BezirkMiddleware.initialize(this); //initialize with default configurations
        Log.d("MyApplication", "Application is starting");
    }
}
