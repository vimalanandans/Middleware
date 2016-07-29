package com.bezirk.proxy.android;

import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.proxy.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZirkMessageHandler implements MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageHandler.class);

    final String FIRE_INTENT_ACTION = "com.bezirk.middleware.broadcast";
    private final Context applicationContext;

    public ZirkMessageHandler(Context context) {
        this.applicationContext = context;
    }

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        final Intent intent = new Intent();
        intent.putExtra("message", eventIncomingMessage);
        sendZirkIntent(intent);
    }

    @Override
    public void onIncomingStream(ReceiveFileStreamAction receiveFileStreamAction) {
        final Intent intent = new Intent();
        intent.putExtra("message", receiveFileStreamAction);
        sendZirkIntent(intent);
    }

    private void sendZirkIntent(Intent intent) {
        intent.setAction(FIRE_INTENT_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (null != applicationContext) {
            applicationContext.sendBroadcast(intent);
        } else {
            logger.error("Cannot send Zirk intent because applicationContext is null");
        }
    }

    private void sentZirkSphereIntent(Intent intent) {
        intent.setAction("SphereScanReceiver");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (applicationContext != null) {
            applicationContext.sendBroadcast(intent);
        } else {
            logger.error("Cannot send Zirk intent because applicationContext is null");
        }
    }
}
