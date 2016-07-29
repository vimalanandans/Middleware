package com.bezirk.proxy.android;

import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.proxy.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements BezirkCallback and provides platform specific implementations for android
 */
public class ProxyClientMessageHandler implements MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProxyClientMessageHandler.class);

    /**
     * This intent action is subscribed by all the ProxyForBezirk
     */
    final String FIRE_INTENT_ACTION = "com.bezirk.middleware.broadcast";

    /**
     * Context to fire the Intent
     */
    private final Context applicationContext;

    public ProxyClientMessageHandler(Context context) {
        this.applicationContext = context;
    }

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        try {
            final Intent fireIntent = new Intent();
            fireIntent.putExtra("message", eventIncomingMessage);
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Cant fire the intent to the services as the ppd intent is not valid.", e);
        }
    }

    @Override
    public void onIncomingStream(ReceiveFileStreamAction receiveFileStreamAction) {
        try {
            final Intent fireIntent = new Intent();
            fireIntent.putExtra("message", receiveFileStreamAction);
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Cannot give callback as all the fields are not set", e);
        }
    }

    /**
     * This method just fires the intent to the services.
     */
    private void fireIntentToService(Intent fireIntent) {
        fireIntent.setAction(FIRE_INTENT_ACTION);
        fireIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (null != applicationContext) {
            applicationContext.sendBroadcast(fireIntent);
        } else {
            logger.error("Application Context is null, cant fire the  broadcast Intent intent!");
        }
    }

    private void fireIntentToSphere(Intent fireIntent) {
        fireIntent.setAction("SphereScanReceiver");
        fireIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (null != applicationContext) {
            applicationContext.sendBroadcast(fireIntent);
        } else {
            logger.error("Application Context is null, cant fire the  broadcast Intent intent!");
        }
    }
}
