package com.bezirk.middleware.proxy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IntentMessageReceiver extends BroadcastReceiver {
    private final String TAG = IntentMessageReceiver.class.getSimpleName();

    private static final int TIME_DURATION = 15000;
    private static final int MAX_MAP_SIZE = 50;

    private static final ConcurrentMap<String, Long> duplicateMsgMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Long> duplicateStreamMap = new ConcurrentHashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String receivedServiceId = intent.getStringExtra("service_id_tag");

        if (!isValidRequest(receivedServiceId)) {
            return;
        }

        processReceivedIntent(intent);
    }

    private void processReceivedIntent(Intent intent) {
        final String discriminator = intent.getStringExtra("discriminator");

        switch (discriminator) {
            case "EVENT":
                processEvent(intent);
                break;
            case "STREAM_UNICAST":
                processStreamUnicast(intent);
                break;
            case "STREAM_STATUS":
                processStreamStatus(intent);
                break;
            default:
        }
    }

    private boolean isValidRequest(String receivedServiceId) {
        if (null == receivedServiceId) {
            Log.e(TAG, "ZirkId is malfunctioning");
            return false;
        }
        Log.d(TAG, "receivedServiceId" + receivedServiceId);

        if (null == ProxyClient.context) {
            // TODO - Check with Joao if the application has to be launched!
            Log.e(TAG, "Application is not started");
            return false;
        }

        ZirkId serviceId = new Gson().fromJson(receivedServiceId, ZirkId.class);

        if (!isRequestForCurrentApp(serviceId.getZirkId())) {
            Log.e(TAG, "Intent is not for this Service");
            return false;
        }
        return true;
    }

    private void processEvent(Intent intent) {
        final String eventTopic = intent.getStringExtra("eventTopic");
        final String eventMessage = intent.getStringExtra("eventMessage");
        final String eventSender = intent.getStringExtra("eventSender");

        boolean valid = isIntentValid(eventTopic, eventMessage, eventSender);

        if (!valid) {
            Log.e(TAG, "The Unicast event intent is received but dropped as it doesn't contain the required fields");
            return;
        }

        final String messageId = intent.getStringExtra("msgId");
        BezirkZirkEndPoint sourceOfEventSEP = new Gson().fromJson(eventSender, BezirkZirkEndPoint.class);
        //Check for duplicate message
        if (checkDuplicateMsg(sourceOfEventSEP.zirkId.getZirkId(), messageId)) {
            boolean isEventReceived = receiveEventOrStream(eventTopic, eventMessage, sourceOfEventSEP, (short) 0, null, "EVENT", ProxyClient.eventListenerMap);
            if (isEventReceived) {
                return;
            }
        } else {
            Log.e(TAG, "Duplicate Message, Dropping message");
            return;
        }
        Log.e(TAG, "Event Topic Malfunctioning");
    }

    private boolean receiveEventOrStream(String topic, String message, BezirkZirkEndPoint sourceSEP, short streamId,
                                         String filePath, String type, Map<String, List<BezirkListener>> listenerMap) {

        // find the class of the received event using the topic of the event, as the topic will hold the canonical name of the event
        Class c;
        try {
            c = Class.forName(topic, false, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Unable to find class '" + topic + "'");
            return false;
        }

        if (listenerMap.containsKey(topic)) {
            final List<BezirkListener> tempEventListners = listenerMap.get(topic);
            for (BezirkListener listener : tempEventListners) {
                if ("EVENT".equalsIgnoreCase(type)) {
                    Event event = (Event) Message.fromJson(message, c);
                    listener.receiveEvent(topic, event, sourceSEP);
                } else if ("STREAM_UNICAST".equalsIgnoreCase(type)) {

                    StreamDescriptor streamDescriptor = (StreamDescriptor) Message.fromJson(message, c);
                    listener.receiveStream(topic, streamDescriptor, streamId, new File(filePath), sourceSEP);
                }
            }
            return true;
        }
        return false;

    }

    private boolean isIntentValid(String eventTopic, String eventMessage, String eventSender) {
        boolean valid = true;
        String err = "Intent doesn't contain extra property: ";

        if (null == eventTopic) {
            Log.e(TAG, err + " eventTopic");
            valid = false;
        }
        if (null == eventMessage) {
            Log.e(TAG, err + " eventMessage");
            valid = false;
        }
        if (null == eventSender) {
            Log.e(TAG, err + " eventSender");
            valid = false;
        }
        return valid;
    }

    private void processStreamUnicast(Intent intent) {
        final String streamTopic = intent.getStringExtra("streamTopic");
        final String streamMsg = intent.getStringExtra("streamMsg");
        final String filePath = intent.getStringExtra("filePath");
        final String senderSep = intent.getStringExtra("senderSEP");
        Log.d(TAG, " " + streamTopic + "," + streamMsg + "+" + filePath + "-" + senderSep);

        if (null == streamTopic || null == streamMsg || null == filePath || null == senderSep) {
            Log.e(TAG, "Unicast StreamDescriptor has some null quantities");
            return;
        }
        final short streamId = intent.getShortExtra("streamId", (short) -1);

        BezirkZirkEndPoint sourceOfStreamSEP = new Gson().fromJson(senderSep, BezirkZirkEndPoint.class);
        Log.e(TAG, sourceOfStreamSEP.zirkId.getZirkId() + ":" + streamId);
        if (checkDuplicateStream(sourceOfStreamSEP.zirkId.getZirkId(), streamId)) {

            boolean isStreamReceived = receiveEventOrStream(streamTopic, streamMsg, sourceOfStreamSEP, streamId, filePath, "STREAM_UNICAST",
                    ProxyClient.streamListenerMap);

            if (!isStreamReceived) {

                Log.e(TAG, " StreamListenerMap doesn't have a mapped StreamDescriptor");
            }

        } else {
            Log.e(TAG, "Duplicate StreamDescriptor Request Received");
        }
    }

    private void processStreamStatus(Intent intent) {
        final short streamId = intent.getShortExtra("streamId", (short) -1);
        final int streamStatus = intent.getIntExtra("streamStatus", -1);
        if (-1 == streamId || -1 == streamStatus) {
            Log.e(TAG, "Error in StreamDescriptor Status received Intent ");
            return;
        }
        final BezirkListener.StreamStates streamCondition = (streamStatus == 1) ? BezirkListener.StreamStates.END_OF_DATA : BezirkListener.StreamStates.LOST_CONNECTION;
        if (ProxyClient.activeStreams.containsKey(streamId)) {
            final String streamTopic = ProxyClient.activeStreams.get(streamId);
            if (ProxyClient.streamListenerMap.containsKey(streamTopic)) {
                for (BezirkListener listener : ProxyClient.streamListenerMap.get(streamTopic)) {
                    listener.streamStatus(streamId, streamCondition);
                }
            }
            ProxyClient.activeStreams.remove(streamId);
        }
        Log.e(TAG, "No StreamId is found");
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
