package com.bezirk.middleware.proxy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZirkMessageReceiver extends BroadcastReceiver {
    private static final int TIME_DURATION = 15000;
    private static final int MAX_MAP_SIZE = 50;
    private static final Map<String, Long> duplicateMsgMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> duplicateStreamMap = new ConcurrentHashMap<>();
    private final String TAG = ZirkMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ZirkAction message = (ZirkAction) intent.getSerializableExtra("message");

        if (isValidRequest(message.getZirkId())) {
            processReceivedIntent(message);
        }
    }

    private void processReceivedIntent(ZirkAction message) {
        switch (message.getAction()) {
            case ACTION_ZIRK_RECEIVE_EVENT:
                processEvent((UnicastEventAction) message);
                break;
            case ACTION_ZIRK_RECEIVE_STREAM:
                processStreamUnicast((ReceiveFileStreamAction) message);
                break;
            default:
                Log.e(TAG, "Unimplemented action: " + message.getAction());
        }
    }

    private boolean isValidRequest(ZirkId receivedZirkId) {
        if (null == receivedZirkId) {
            Log.e(TAG, "ZirkId is malfunctioning");
            return false;
        }

        if (null == ProxyClient.context) {
            Log.e(TAG, "Application is not started");
            return false;
        }

        if (!isRequestForCurrentApp(receivedZirkId.getZirkId())) {
            Log.e(TAG, "Intent is not for this Service");
            return false;
        }
        return true;
    }

    private void processEvent(UnicastEventAction eventMessage) {
        final String serializedEvent = eventMessage.getSerializedEvent();
        final BezirkZirkEndPoint eventSender = (BezirkZirkEndPoint) eventMessage.getEndpoint();

        if (serializedEvent == null) {
            Log.e(TAG, "Received a null event or event topic");
            return;
        }

        final String messageId = eventMessage.getMessageId();
        //Check for duplicate message
        if (checkDuplicateMsg(eventSender.zirkId.getZirkId(), messageId)) {
            boolean isEventReceived = receiveEvent(serializedEvent, eventSender,
                    ProxyClient.eventListenerMap);
            if (isEventReceived) {
                return;
            }
        } else {
            Log.e(TAG, "Duplicate Message, Dropping message");
            return;
        }
        Log.e(TAG, "Event Topic Malfunctioning");
    }

    private boolean receiveEvent(String message, BezirkZirkEndPoint sourceEndpoint,
                                 Map<String, List<EventSet.EventReceiver>> listenerMap) {
        Event event = Message.fromJson(message, Event.class);
        String eventName = event.getClass().getName();

        if (listenerMap.containsKey(eventName)) {
            final List<EventSet.EventReceiver> messageListeners = listenerMap.get(eventName);
            for (EventSet.EventReceiver listener : messageListeners) {
                listener.receiveEvent(event, sourceEndpoint);
            }

            return true;
        }

        return false;
    }

    private boolean receiveStream(String message, BezirkZirkEndPoint sourceEndpoint,
                                  String filePath, Map<String, List<StreamSet.StreamReceiver>> listenerMap) {
        StreamDescriptor streamDescriptor = Message.fromJson(message, StreamDescriptor.class);
        String streamName = streamDescriptor.getClass().getName();

        if (listenerMap.containsKey(streamName)) {
            final List<StreamSet.StreamReceiver> messageListeners = listenerMap.get(streamName);
            for (StreamSet.StreamReceiver listener : messageListeners) {
                listener.receiveStream(streamDescriptor, new File(filePath), sourceEndpoint);
            }

            return true;
        }

        return false;
    }

    private void processStreamUnicast(ReceiveFileStreamAction streamMessage) {
        final String streamMsg = streamMessage.getSerializedStream();
        final File file = streamMessage.getFile();
        final BezirkZirkEndPoint senderSep = streamMessage.getSender();
        Log.d(TAG, " " + streamMsg + "+" + file + "-" + senderSep);

        if (streamMsg == null || file == null || senderSep == null) {
            Log.e(TAG, "Unicast StreamDescriptor has some null quantities");
            return;
        }

        boolean isStreamReceived = receiveStream(streamMsg, senderSep,
                file.getPath(), ProxyClient.streamListenerMap);

        if (!isStreamReceived) {

            Log.e(TAG, " StreamListenerMap doesn't have a mapped StreamDescriptor");
        }
    }

    private boolean checkDuplicateMsg(final String sid, final String messageId) {
        final String key = sid + ":" + messageId;
        final Long currentTime = new Date().getTime();
        if (duplicateMsgMap.containsKey(key)) {
            if (currentTime - duplicateMsgMap.get(key) > TIME_DURATION) {
                duplicateMsgMap.remove(key);
                duplicateMsgMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            if (duplicateMsgMap.size() < MAX_MAP_SIZE) {
                duplicateMsgMap.put(key, currentTime);
                return true;
            } else {
                duplicateMsgMap.remove(duplicateMsgMap.keySet().iterator().next());
                duplicateMsgMap.put(key, currentTime);
                return true;
            }
        }
    }

    private boolean checkDuplicateStream(final String sid, final int streamId) {
        final String key = sid + ":" + streamId;
        final Long currentTime = new Date().getTime();
        if (duplicateStreamMap.containsKey(key)) {
            if (currentTime - duplicateStreamMap.get(key) > TIME_DURATION) {
                duplicateStreamMap.remove(key);
                duplicateStreamMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            if (duplicateStreamMap.size() < MAX_MAP_SIZE) {
                duplicateStreamMap.put(key, currentTime);
                return true;
            } else {
                duplicateStreamMap.remove(duplicateStreamMap.keySet().iterator().next());
                duplicateStreamMap.put(key, currentTime);
                return true;
            }
        }
    }

    /**
     * This methods checks if the passed zirkId belongs to the current application.
     * <p>
     * Note: An application can have multiple serviceIds
     * </p>
     *
     * @param serviceId
     * @return
     */
    private boolean isRequestForCurrentApp(final String serviceId) {
        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(ProxyClient.context);
        Map<String, ?> keys = shrdPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            //find and delete the entry corresponding to this zirkId
            if (entry.getValue().toString().equalsIgnoreCase(serviceId)) {
                return true;
            }
        }
        return false;
    }
}
