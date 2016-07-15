package com.bezirk.middleware.proxy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bezirk.actions.BezirkActions.KEY_PIPE;
import static com.bezirk.actions.BezirkActions.KEY_PIPE_POLICY_IN;
import static com.bezirk.actions.BezirkActions.KEY_PIPE_POLICY_OUT;
import static com.bezirk.actions.BezirkActions.KEY_PIPE_REQ_ID;

public class IntentMessageReceiver extends BroadcastReceiver {
    private final String TAG = "BezirkIntentReceiver";

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

        if (listenerMap.containsKey(topic)) {
            final List<BezirkListener> tempEventListners = listenerMap.get(topic);
            for (BezirkListener listener : tempEventListners) {
                if ("EVENT".equalsIgnoreCase(type)) {
                    Event event = Message.fromJson(message, Event.class);
                    listener.receiveEvent(topic, event, sourceSEP);
                } else if ("STREAM_UNICAST".equalsIgnoreCase(type)) {
                    Stream stream = Message.fromJson(message, Stream.class);
                    listener.receiveStream(topic, stream, streamId, new File(filePath), sourceSEP);
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
        Log.e(TAG, sourceOfStreamSEP.zirkId.getZirkId() + ":" + streamId);
        if (checkDuplicateStream(sourceOfStreamSEP.zirkId.getZirkId(), streamId)) {

            boolean isStreamReceived = receiveEventOrStream(streamTopic, streamMsg, sourceOfStreamSEP, streamId, filePath, "STREAM_UNICAST",
                    ProxyClient.streamListenerMap);

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

    private void processDiscovery(Intent intent) {
        if (StringValidatorUtil.isObjectNotNull(ProxyClient.DiscoveryListener)) {
            final int receivedDiscoveryId = intent.getIntExtra("DiscoveryId", -1);

            if (ProxyClient.discoveryCount == receivedDiscoveryId) {
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

                ProxyClient.DiscoveryListener.discovered(new HashSet<DiscoveredZirk>(discoveredList));
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
        if (ProxyClient.pipeListenerMap.containsKey(pipeId)) {
            final String jsonInPolicy = intent.getStringExtra(KEY_PIPE_POLICY_IN);
            final String jsonOutPolicy = intent.getStringExtra(KEY_PIPE_POLICY_OUT);
            Log.i(TAG, "-- RECEIVED In Policy --");
            Log.i(TAG, jsonInPolicy);
            Log.i(TAG, "-- RECEIVED Out Policy --");
            Log.i(TAG, jsonOutPolicy);
            final BezirkPipePolicy policyIn = PipePolicy.fromJson(jsonInPolicy, BezirkPipePolicy.class);
            final BezirkPipePolicy policyOut = PipePolicy.fromJson(jsonOutPolicy, BezirkPipePolicy.class);
            // final Pipe pipe = Pipe.fromJson(jsonPipe, CloudPipe.class);
            // ProxyClient.pipeListenerMap.get(pipeId).pipeGranted(pipe, policyIn, policyOut);
        } else {
            Log.e(TAG, "Pipe with this Id:" + pipeId + " is not in Map");
        }
    }

    private boolean checkDuplicateMsg(final String sid, final String messageId) {
        final String key = sid + ":" + messageId;
        final Long currentTime = new Date().getTime();
        if (ProxyClient.duplicateMsgMap.containsKey(key)) {
            if (currentTime - ProxyClient.duplicateMsgMap.get(key) > ProxyClient.TIME_DURATION) {
                ProxyClient.duplicateMsgMap.remove(key);
                ProxyClient.duplicateMsgMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            if (ProxyClient.duplicateMsgMap.size() < ProxyClient.MAX_MAP_SIZE) {
                ProxyClient.duplicateMsgMap.put(key, currentTime);
                return true;
            } else {
                ProxyClient.duplicateMsgMap.remove(ProxyClient.duplicateMsgMap.keySet().iterator().next());
                ProxyClient.duplicateMsgMap.put(key, currentTime);
                return true;
            }
        }
    }

    private boolean checkDuplicateStream(final String sid, final int streamId) {
        final String key = sid + ":" + streamId;
        final Long currentTime = new Date().getTime();
        if (ProxyClient.duplicateStreamMap.containsKey(key)) {
            if (currentTime - ProxyClient.duplicateStreamMap.get(key) > ProxyClient.TIME_DURATION) {
                ProxyClient.duplicateStreamMap.remove(key);
                ProxyClient.duplicateStreamMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            if (ProxyClient.duplicateStreamMap.size() < ProxyClient.MAX_MAP_SIZE) {
                ProxyClient.duplicateStreamMap.put(key, currentTime);
                return true;
            } else {
                ProxyClient.duplicateStreamMap.remove(ProxyClient.duplicateStreamMap.keySet().iterator().next());
                ProxyClient.duplicateStreamMap.put(key, currentTime);
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
