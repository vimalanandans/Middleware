package com.bezirk.middleware.android.proxy.android;

import android.content.Intent;
import android.util.Log;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.RegisterZirkAction;
import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.SetLocationAction;
import com.bezirk.middleware.core.actions.SubscriptionAction;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.core.proxy.ProxyServer;

import java.io.File;

public class AndroidProxyServer extends ProxyServer {
    private static final String TAG = AndroidProxyServer.class.getName();

    public AndroidProxyServer(IdentityManager identityManager) { super(identityManager); }

    public void registerZirk(Intent intent) {
        final RegisterZirkAction registrationAction =
                (RegisterZirkAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_REGISTER.getName());

        Log.v(TAG, "Zirk registration received by Bezirk. Name: " + registrationAction.getZirkName()
                + ", ID: {}" + registrationAction.getZirkId());

        super.registerZirk(registrationAction);
    }

    public void subscribeService(Intent intent) {
        Log.v(TAG, "Received subscription from zirk");

        final SubscriptionAction subscriptionAction =
                (SubscriptionAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_SUBSCRIBE.getName());

        super.subscribe(subscriptionAction);
    }

    public void unsubscribeService(Intent intent) {
        Log.v(TAG, "Received unsubscribe from zirk");

        final SubscriptionAction subscriptionAction =
                (SubscriptionAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_UNSUBSCRIBE.getName());

        if (subscriptionAction.getMessageSet() != null) {
            super.unsubscribe(subscriptionAction);
        } else {
            super.unregister(new RegisterZirkAction(subscriptionAction.getZirkId(), null));
        }
    }

    public void sendMulticastEvent(Intent intent) {
        Log.v(TAG, "Received multicast message from zirk");

        final SendMulticastEventAction eventAction =
                (SendMulticastEventAction) intent.getSerializableExtra(BezirkAction.ACTION_ZIRK_SEND_MULTICAST_EVENT.getName());

        super.sendEvent(eventAction);
    }

    public void sendUnicastEvent(Intent intent) {
        Log.v(TAG, "Received unicast message from zirk");

        final UnicastEventAction eventAction =
                (UnicastEventAction) intent.getSerializableExtra(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT.getName());

        super.sendEvent(eventAction);
    }

    public void sendUnicastStream(Intent intent) {
        Log.v(TAG, "Stream to unicast from Zirk");

        final SendFileStreamAction streamAction =
                (SendFileStreamAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_PUSH_UNICAST_STREAM.getName());

        final Boolean isEncrypt = (Boolean) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_PUSH_UNICAST_STREAM_ENCRYPT.getName());
        final Boolean isIncremental= (Boolean) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_PUSH_UNICAST_STREAM_INCREMENTAL.getName());
        final File file = (File) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_PUSH_UNICAST_STREAM_FILE.getName());

        StreamDescriptor streamDescriptor  = new StreamDescriptor(isIncremental, isEncrypt, file, streamAction.getStreamActionName());
        streamAction.setDescriptor(streamDescriptor);

        super.sendStream(streamAction);
    }

    public void setLocation(Intent intent) {
        SetLocationAction locationAction = (SetLocationAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_SET_LOCATION.getName());

        Log.v(TAG, "Received location " + locationAction.getLocation() + " from zirk");

        super.setLocation(locationAction);
    }
}
