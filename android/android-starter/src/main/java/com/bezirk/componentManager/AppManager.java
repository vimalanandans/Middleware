package com.bezirk.componentManager;

import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.BezirkAction;

/**
 * App manager helps to manage the apps. Used by Apps to instantiate the bezirk service
 *  Draft version. TODO convert the intent extra fields to data class
 */
public class AppManager {

    static  AppManager app = new AppManager();
    private String COMPONENT_NAME = "com.bezirk.controlui";
    static String APPLICATION_NAME_TAG = "AppName";
    static String MSG_GROUP_NAME_TAG = "MessageGroupName";
    static String STICKY_TAG = "sticky";

    static public AppManager getAppManager()
    {
        return app;
    }

    /**
     * To start the middleware android service as part of the app scope
     * @param context - contect of the application / activity
     * @param backgroundService - To retain the middleware as android background service
     * @param appName - Name of the background service display in the android pull down menu
     * @param messageGroupName - Group name to avoid message collision
     * @return - Return true on success
     */
    public boolean startBezirk(Context context, boolean backgroundService, String appName, String messageGroupName)
    {
        //Start Bezirk
        Intent serviceIntent = new Intent(context, ComponentManager.class);

        /* set the context while starting the app*/
        COMPONENT_NAME = context.getPackageName();

        serviceIntent.setAction(BezirkAction.ACTION_START_BEZIRK.getName());
        serviceIntent.putExtra(STICKY_TAG, backgroundService);
        serviceIntent.putExtra(APPLICATION_NAME_TAG, appName);
        //serviceIntent.putExtra(MSG_GROUP_NAME_TAG,messageGroupName);
        context.startService(serviceIntent);

        return true;
    }

    /**
     * get the component package name to fire the intent
     * @return - package name of the component
     */

    public String getComponentName(){
        return COMPONENT_NAME;
    }

    /**
     * To stop the middleware background service
     * @param context - Context of the application / activiy
     * @return - Return true on success
     */
    public boolean stopBezirk(Context context)
    {
        //Stop Bezirk intent to stop the message
        Intent serviceIntent = new Intent(context, ComponentManager.class);

        serviceIntent.setAction(BezirkAction.ACTION_STOP_BEZIRK.getName());
        context.startService(serviceIntent);
        return true;
    }

}
