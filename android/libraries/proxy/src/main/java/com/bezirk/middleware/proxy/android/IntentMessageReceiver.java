package com.bezirk.middleware.proxy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.pipe.policy.ext.UhuPipePolicy;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bezirk.actions.UhuActions.KEY_PIPE;
import static com.bezirk.actions.UhuActions.KEY_PIPE_POLICY_IN;
import static com.bezirk.actions.UhuActions.KEY_PIPE_POLICY_OUT;
import static com.bezirk.actions.UhuActions.KEY_PIPE_REQ_ID;

/**
 * Created by AJC6KOR on 11/19/2015.
 */
public class IntentMessageReceiver extends BroadcastReceiver {
    private final String TAG = "UhuIntentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        final String receivedServiceId = intent.getStringExtra("service_id_tag");

        if (!isValidRequest(receivedServiceId)) {

            return;
        }

        // TODO: need to implement logback / slf4j style logging (ASW)

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
            case "STREAM_MULTICAST":
                Log.e(TAG, "Multicast Stream received");
                break;
            case "STREAM_STATUS":
                processStreamStatus(intent);
                break;
            case "DISCOVERY":
                processDiscovery(intent);
                break;
            case "PIPE-APPROVED":
                processPipeApproval(intent);
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

        if (null == Proxy.mContext) {
            // TODO - Check with Joao if the application has to be launched!
            Log.e(TAG, "Application is not started");
            return false;
        }

        BezirkZirkId serviceId = new Gson().fromJson(receivedServiceId, BezirkZirkId.class);

        if (!isRequestForCurrentApp(serviceId.getBezirkZirkId())) {
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
        if (checkDuplicateMsg(sourceOfEventSEP.zirkId.getBezirkZirkId(), messageId)) {
            boolean isEventReceived = receiveEventOrStream(eventTopic, eventMessage, sourceOfEventSEP, (short) 0, null, "EVENT", Proxy.eventListenerMap);
            if (isEventReceived) {
                return;
            }
        } else {
            Log.e(TAG, "Duplicate Message, Dropping message");
            return;
        }
        Log.e(TAG, "Event Topic Malfunctioning");
        return;
    }

    private boolean receiveEventOrStream(String topic, String message, BezirkZirkEndPoint sourceSEP, short streamId,
                                         String filePath, String type, Map<String, ArrayList<BezirkListener>> listenerMap) {

        if (listenerMap.containsKey(topic)) {
            final List<BezirkListener> tempEventListners = listenerMap.get(topic);
            for (BezirkListener listener : tempEventListners) {
                if ("EVENT".equalsIgnoreCase(type)) {
                    listener.receiveEvent(topic, message, sourceSEP);
                } else if ("STREAM_UNICAST".equalsIgnoreCase(type)) {
                    listener.receiveStream(topic, message, streamId, new File(filePath), sourceSEP);
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
            Log.e(TAG, "Unicast Stream has some null quantities");
            return;
        }
        final short streamId = intent.getShortExtra("streamId", (short) -1);

        BezirkZirkEndPoint sourceOfStreamSEP = new Gson().fromJson(senderSep, BezirkZirkEndPoint.class);
        Log.e(TAG, sourceOfStreamSEP.zirkId.getBezirkZirkId() + ":" + streamId);
        if (checkDuplicateStream(sourceOfStreamSEP.zirkId.getBezirkZirkId(), streamId)) {

            boolean isStreamReceived = receiveEventOrStream(streamTopic, streamMsg, sourceOfStreamSEP, streamId, filePath, "STREAM_UNICAST",
                    Proxy.streamListenerMap);

            if (!isStreamReceived) {

                Log.e(TAG, " StreamListnerMap doesnt have a mapped Stream");
            }

        } else {
            Log.e(TAG, "Duplicate Stream Request Received");
        }
    }

    private void processStreamStatus(Intent intent) {
        final short streamId = intent.getShortExtra("streamId", (short) -1);
        final int streamStatus = intent.getIntExtra("streamStatus", -1);
        if (-1 == streamId || -1 == streamStatus) {
            Log.e(TAG, "Error in Stream Status received Intent ");
            return;
        }
        final BezirkListener.StreamStates streamCondition = (streamStatus == 1) ? BezirkListener.StreamStates.END_OF_DATA : BezirkListener.StreamStates.LOST_CONNECTION;
        if (Proxy.activeStreams.containsKey(streamId)) {
            final String streamTopic = Proxy.activeStreams.get(streamId);
            if (Proxy.streamListenerMap.containsKey(streamTopic)) {
                for (BezirkListener listener : Proxy.streamListenerMap.get(streamTopic)) {
                    listener.streamStatus(streamId, streamCondition);
                }
            }
            Proxy.activeStreams.remove(streamId);
        }
        Log.e(TAG, "No StreamId is found");
    }

    private void processDiscovery(Intent intent) {
        if (StringValidatorUtil.isObjectNotNull(Proxy.DiscoveryListener)) {
            final int receivedDiscoveryId = intent.getIntExtra("DiscoveryId", -1);

            if (Proxy.discoveryCount == receivedDiscoveryId) {
                final Gson gson = new Gson();
                final String discoveredListAsString = intent.getStringExtra("DiscoveredServices");
                //Deserialiaze
                Type discoveredListType = new TypeToken<HashSet<BezirkDiscoveredZirk>>() {
                }.getType();

                final Set<BezirkDiscoveredZirk> discoveredList = gson.fromJson(discoveredListAsString, discoveredListType);

                if (null == discoveredList) {
                    Log.e(TAG, "Empty discovered List");
                    return;
                }

                Proxy.DiscoveryListener.discovered(new HashSet<DiscoveredZirk>(discoveredList));
            } else {
                Log.e(TAG, "Discovery Id not matched");
            }
        } else {
            Log.e(TAG, "Discovery Callback not found");
        }
    }

    private void processPipeApproval(Intent intent) {
        final String jsonPipe = intent.getStringExtra(KEY_PIPE);
        final String pipeId = intent.getStringExtra(KEY_PIPE_REQ_ID);
        if (Proxy.pipeListenerMap.containsKey(pipeId)) {
            final String jsonInPolicy = intent.getStringExtra(KEY_PIPE_POLICY_IN);
            final String jsonOutPolicy = intent.getStringExtra(KEY_PIPE_POLICY_OUT);
            Log.i(TAG, "-- RECEIVED In Policy --");
            Log.i(TAG, jsonInPolicy);
            Log.i(TAG, "-- RECEIVED Out Policy --");
            Log.i(TAG, jsonOutPolicy);
            final UhuPipePolicy policyIn = PipePolicy.fromJson(jsonInPolicy, UhuPipePolicy.class);
            final UhuPipePolicy policyOut = PipePolicy.fromJson(jsonOutPolicy, UhuPipePolicy.class);
            final Pipe pipe = Pipe.deserialize(jsonPipe, CloudPipe.class);
            Proxy.pipeListenerMap.get(pipeId).pipeGranted(pipe, policyIn, policyOut);
        } else {
            Log.e(TAG, "Pipe with this Id:" + pipeId + " is not in Map");
        }
    }

    private boolean checkDuplicateMsg(final String sid, final String messageId) {
        final String key = sid + ":" + messageId;
        final Long currentTime = new Date().getTime();
        if (Proxy.duplicateMsgMap.containsKey(key)) {
            if (currentTime - Proxy.duplicateMsgMap.get(key) > Proxy.TIME_DURATION) {
                Proxy.duplicateMsgMap.remove(key);
                Proxy.duplicateMsgMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            if (Proxy.duplicateMsgMap.size() < Proxy.MAX_MAP_SIZE) {
                Proxy.duplicateMsgMap.put(key, currentTime);
                return true;
            } else {
                Proxy.duplicateMsgMap.remove(Proxy.duplicateMsgMap.keySet().iterator().next());
                Proxy.duplicateMsgMap.put(key, currentTime);
                return true;
            }
        }
    }

    private boolean checkDuplicateStream(final String sid, final int streamId) {
        final String key = sid + ":" + streamId;
        final Long currentTime = new Date().getTime();
        if (Proxy.duplicateStreamMap.containsKey(key)) {
            if (currentTime - Proxy.duplicateStreamMap.get(key) > Proxy.TIME_DURATION) {
                Proxy.duplicateStreamMap.remove(key);
                Proxy.duplicateStreamMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            if (Proxy.duplicateStreamMap.size() < Proxy.MAX_MAP_SIZE) {
                Proxy.duplicateStreamMap.put(key, currentTime);
                return true;
            } else {
                Proxy.duplicateStreamMap.remove(Proxy.duplicateStreamMap.keySet().iterator().next());
                Proxy.duplicateStreamMap.put(key, currentTime);
                return true;
            }
        }
    }

    /**
     * @param serviceId
     * @return
     * @author Rishabh
     * <p/>
     * This methods checks if the passed zirkId belongs to the current application.<br>
     * Note: An application can have multiple serviceIds
     */
    private boolean isRequestForCurrentApp(final String serviceId) {
        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(Proxy.mContext);
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
