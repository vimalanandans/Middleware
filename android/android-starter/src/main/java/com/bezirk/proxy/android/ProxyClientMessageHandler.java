package com.bezirk.proxy.android;

import android.content.Context;
import android.content.Intent;

import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;
import com.google.gson.Gson;

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

    private final Gson gson = new Gson();

    public ProxyClientMessageHandler(Context context) {
        this.applicationContext = context;
    }

    @Override
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
        try {
            final Intent fireIntent = new Intent();
            fireIntent.putExtra("service_id_tag", gson.toJson(eventIncomingMessage.getRecipient()));
            fireIntent.putExtra("eventSender", gson.toJson(eventIncomingMessage.getSenderEndPoint()));
            fireIntent.putExtra("eventMessage", eventIncomingMessage.getSerializedEvent());
            fireIntent.putExtra("eventTopic", eventIncomingMessage.getEventTopic());
            fireIntent.putExtra("msgId", eventIncomingMessage.getMsgId());
            fireIntent.putExtra("discriminator", eventIncomingMessage.getCallbackType());
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Cant fire the intent to the services as the ppd intent is not valid.", e);
        }
    }

    @Override
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
        try {
            final Intent fireIntent = new Intent();
            fireIntent.putExtra("service_id_tag", gson.toJson(streamIncomingMessage.getRecipient()));
            fireIntent.putExtra("streamId", streamIncomingMessage.getCallbackType());
            fireIntent.putExtra("streamTopic", streamIncomingMessage.getStreamTopic());
            fireIntent.putExtra("streamMsg", streamIncomingMessage.getSerializedStream());
            fireIntent.putExtra("filePath", streamIncomingMessage.getFile().getAbsolutePath());
            fireIntent.putExtra("streamId", streamIncomingMessage.getLocalStreamId()); //
            fireIntent.putExtra("senderEndPoint", gson.toJson(streamIncomingMessage.getSender()));
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Cannot give callback as all the fields are not set", e);
        }
    }

    @Override
    public void onStreamStatus(StreamStatusMessage streamStatusMessage) {
        try {
            final Intent fireIntent = new Intent();
            fireIntent.putExtra("service_id_tag", gson.toJson(streamStatusMessage.getRecipient()));
            fireIntent.putExtra("discriminator", streamStatusMessage.getCallbackType());
            fireIntent.putExtra("streamId", streamStatusMessage.getStreamId());
            fireIntent.putExtra("streamStatus", streamStatusMessage.getStreamStatus());
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Callback cannot be given to the services as there is some exception in the Firing the Intent", e);
        }
    }


    @Override
    public void onDiscoveryIncomingMessage(DiscoveryIncomingMessage discoveryIncomingMessage) {
        try {
            final Intent fireIntent = new Intent();
            fireIntent.putExtra("service_id_tag", gson.toJson(discoveryIncomingMessage.getRecipient()));
            fireIntent.putExtra("discriminator", discoveryIncomingMessage.getCallbackType());
            fireIntent.putExtra("DiscoveryId", discoveryIncomingMessage.getDiscoveryId());
            fireIntent.putExtra("DiscoveredServices", discoveryIncomingMessage.getDiscoveredList());
            if (discoveryIncomingMessage.isSphereDiscovery()) {
                fireIntentToSphere(fireIntent);
            } else {
                fireIntentToService(fireIntent);
            }
        } catch (Exception e) {
            logger.error("Callback cannot be given to the services as there is some exception in the Firing the Intent", e);
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
            return;
        }
        logger.error("Application Context is null, cant fire the  broadcast Intent intent!");
    }

    private void fireIntentToSphere(Intent fireIntent) {
        fireIntent.setAction("SphereScanReceiver");
        fireIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (null != applicationContext) {
            applicationContext.sendBroadcast(fireIntent);
            return;
        }
        logger.error("Application Context is null, cant fire the  broadcast Intent intent!");
    }
}
