package com.bezirk.middleware.proxy.android;

import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.BezirkAction;
import com.bezirk.componentManager.AppManager;
import com.bezirk.componentManager.ComponentManager;
import com.bezirk.middleware.Bezirk;
import com.bezirk.proxy.api.impl.ZirkId;

public abstract class BezirkMiddleware {
    /**
     * Register a Zirk with the Bezirk middleware. This makes the Zirk available to the user in
     * Bezirk configuration interfaces, thus allowing her to place it in a sphere to interact with
     * other Zirks. This method returns an instance of the Bezirk API for the newly registered
     * Zirk.
     *
     * @param zirkName the name of the Zirk being registered, as defined by the Zirk
     *                 developer/vendor
     * @return an instance of the Bezirk API for the newly registered Zirk, or <code>null</code> if
     * a Zirk with the name <code>zirkName</code> is already registered.
     */
    public static Bezirk registerZirk(Context context, String zirkName) {
        synchronized (BezirkMiddleware.class) {
            ZirkId zirkId = ProxyClient.registerZirk(context, zirkName);
            return zirkId == null ? null : new ProxyClient(context, zirkId);
        }
    }

    /**
     * To start the middleware android service as part of the app scope
     * @param context - contect of the application / activity
     * @param sticky - To retain the middleware as a background service
     * @param appName - Name of the background service display in the pull up menu
     * @return - Return true on success
     */
    public static boolean startBezirk(Context context, boolean sticky, String appName, String messageGroupName)
    {
        AppManager.getAppManager().startBezirk(context, sticky, appName , messageGroupName);
        return true;
    }

    /**
     * To stop the middleware background service
     * @param context - Context of the application / activity
     * @return - Return true on success
     */
    public boolean stopBezirk(Context context)
    {
        AppManager.getAppManager().stopBezirk(context);
        return true;
    }
}
