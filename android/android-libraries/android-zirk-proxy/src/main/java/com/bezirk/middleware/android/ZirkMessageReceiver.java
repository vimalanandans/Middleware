package com.bezirk.middleware.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ZirkMessageReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageReceiver.class);    

    @Override
    public void onReceive(Context context, Intent intent) {
        ZirkAction message;
        try {
            message = (ZirkAction) intent.getSerializableExtra("message");
        } catch (Exception e) { //to prevent app crash due to  java.io.InvalidClassException
            logger.warn("Failed to read serialized message from intent", e);
            return;
        }

        if (isValidRequest(message.getZirkId())) {
            switch (message.getAction()) {
                case ACTION_ZIRK_RECEIVE_EVENT:
                    processEvent((UnicastEventAction) message);
                    break;
                default:
                    logger.error("Unimplemented action: " + message.getAction());
            }
        }
    }

    private boolean isValidRequest(ZirkId receivedZirkId) {
        if (ProxyClient.context == null) {
            logger.error("Application is not started");
            return false;
        }

        if (!isRequestForCurrentApp(receivedZirkId.getZirkId())) {
            logger.error("Intent is not for this Service");
            return false;
        }

        return true;
    }

    private void processEvent(UnicastEventAction incomingEvent) {
        final BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) incomingEvent.getEndpoint();

        final Event event = (Event) Message.fromJson(incomingEvent.getSerializedEvent());
        final String eventName = event.getClass().getName();

        if (incomingEvent.isIdentified()) {
            ((IdentifiedEvent) event).setAlias(incomingEvent.getAlias());
            ((IdentifiedEvent) event).setMiddlewareUser(incomingEvent.isMiddlewareUser());
        }

        if (ProxyClient.eventSetMap.containsKey(eventName)) {
            final List<EventSet> eventSets = ProxyClient.eventSetMap.get(eventName);
            for (EventSet eventSet : eventSets) {
                eventSet.getEventReceiver().receiveEvent(event, endpoint);
            }
        }

    }

    private boolean isRequestForCurrentApp(final String zirkId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProxyClient.context);
        Map<String, ?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue().toString().equalsIgnoreCase(zirkId)) {
                return true;
            }
        }

        return false;
    }
}
