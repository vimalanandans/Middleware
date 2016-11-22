package com.bezirk.middleware.android;

import android.app.Application;

import com.bezirk.middleware.core.proxy.Config;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeBezirk();
    }

    private void initializeBezirk() {
        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();

        /*setting root log level*/
        configBuilder.setLogLevel(Config.Level.TRACE);

        /*setting package log level*/
        //configBuilder.setPackageLogLevel("com.bezirk.middleware.core.comms", Config.Level.INFO);

        /*setting app name for notification*/
        configBuilder.setAppName("bezirk-android-testapp");

        /*disabling inter-device communication*/
        //configBuilder.setComms(false);

        /*using custom communication groups to prevent crosstalk*/
        //configBuilder.setGroupName("Test Group");

        /*keeping bezirk service alive even after the app is shutdown*/
        //configBuilder.setServiceAlive(true);

        /*initialize with default configurations*/
        //BezirkMiddleware.initialize(this);

        /*initialize with configurations*/
        BezirkMiddleware.initialize(this, configBuilder.create());

        /*initialize with channelId/groupName*/
        //BezirkMiddleware.initialize(this, "MyChannel");

    }

    public synchronized void stop() {
        if (BezirkMiddleware.isInitialized()) {
            BezirkMiddleware.stop();
        }
    }
}
