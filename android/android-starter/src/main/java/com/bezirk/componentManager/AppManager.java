package com.bezirk.componentManager;

import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.BezirkAction;

/**
 * App manager helps to manage the apps. Used by Apps to instantiate the bezirk service
 *
 */
public class AppManager {

    static  AppManager app = new AppManager();
    private String COMPONENT_NAME = "com.bezirk.controlui";
    static String APPLICATION_NAME_TAG = "AppName";
    static String STICKY_TAG = "sticky";

    static public AppManager getAppManager()
    {
        return app;
    }

    /**
     * To start the middleware android service as part of the app scope
     * @param cntx - contect of the application / activity
     * @param sticky - To retain the middleware as a background service
     * @param AppName - Name of the background service display in the pull up menu
     * @return - Return true on success
     */
    public boolean startBezirk(Context cntx, boolean sticky, String AppName)
    {
        //Start Bezirk
        Intent serviceIntent = new Intent(cntx, ComponentManager.class);

        /* set the context while starting the app*/
        COMPONENT_NAME = cntx.getPackageName();

        serviceIntent.setAction(BezirkAction.ACTION_START_BEZIRK.getName());
        serviceIntent.putExtra(STICKY_TAG, sticky);
        serviceIntent.putExtra(APPLICATION_NAME_TAG, AppName);
        cntx.startService(serviceIntent);

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
     * @param cntx - Context of the application / activiy
     * @return - Return true on success
     */
    public boolean stopBezirk(Context cntx)
    {
        //Start Bezirk
        Intent serviceIntent = new Intent(cntx, ComponentManager.class);

        serviceIntent.setAction(BezirkAction.ACTION_STOP_BEZIRK.getName());
        cntx.startService(serviceIntent);
        return true;
    }

}
