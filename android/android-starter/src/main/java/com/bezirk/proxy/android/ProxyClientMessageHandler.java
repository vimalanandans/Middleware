package com.bezirk.proxy.android;

import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.BezirkActions;
import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.proxy.messagehandler.PipeRequestIncomingMessage;
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
    final String FIRE_INTENT_ACTION = "com.bosch.upa.uhu.broadcast";
    /**
     * Used to convert the object to its string
     */
    final Gson gson = new Gson();
    /**
     * Context to fire the Intent
     */
    private final Context applicationContext;

    public ProxyClientMessageHandler(Context context) {
        this.applicationContext = context;
    }

    @Override
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
        String serviceIdKEY = "service_id_tag";
        String eventSenderKEY = "eventSender";
        String eventMsgKEY = "eventMessage";
        String eventTopicKEY = "eventTopic";
        String msgIdKEY = "msgId";
        String discriminatorKEY = "discriminator";

        try {
            Intent fireIntent = new Intent();
            fireIntent.putExtra(serviceIdKEY, gson.toJson(eventIncomingMessage.getRecipient()));
            fireIntent.putExtra(eventSenderKEY, gson.toJson(eventIncomingMessage.senderEndPoint));
            fireIntent.putExtra(eventMsgKEY, eventIncomingMessage.serializedEvent);
            fireIntent.putExtra(eventTopicKEY, eventIncomingMessage.eventTopic);
            fireIntent.putExtra(msgIdKEY, eventIncomingMessage.msgId);
            fireIntent.putExtra(discriminatorKEY, eventIncomingMessage.getCallbackType());
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Cant fire the intent to the services as the ppd intent is not valid.", e);
        }
    }

    @Override
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
        String serviceIdKEY = "service_id_tag";
        String streamIdKEY = "streamId";
        String streamTopicKEY = "streamTopic";
        String discriminatorKEY = "discriminator";
        String streamMsgKEY = "streamMsg";
        String filePathKEY = "filePath";
        String senderSEPKEY = "senderEndPoint";

        try {
            Intent fireintent = new Intent();
            fireintent.putExtra(serviceIdKEY, gson.toJson(streamIncomingMessage.getRecipient()));
            fireintent.putExtra(discriminatorKEY, streamIncomingMessage.getCallbackType());
            fireintent.putExtra(streamTopicKEY, streamIncomingMessage.streamTopic);
            fireintent.putExtra(streamMsgKEY, streamIncomingMessage.serializedStream);
            fireintent.putExtra(filePathKEY, streamIncomingMessage.file.getAbsolutePath());
            fireintent.putExtra(streamIdKEY, streamIncomingMessage.localStreamId); //
            fireintent.putExtra(senderSEPKEY, gson.toJson(streamIncomingMessage.senderSEP));
            fireIntentToService(fireintent);
        } catch (Exception e) {
            logger.error("Cannot give callback as all the fields are not set", e);
        }
    }

    /*fireMulticastStream available in commitID 0fc60754247ec4131ac1a595a0b8c4e78c0b20a8*/

    @Override
    public void onStreamStatus(StreamStatusMessage streamStatusMessage) {
        String serviceIdKEY = "service_id_tag";
        String streamIdKEY = "streamId";
        String streamStatusKEY = "streamStatus";
        String discriminatorKEY = "discriminator";
        Intent fireIntent = new Intent();
        try {
            fireIntent.putExtra(serviceIdKEY, gson.toJson(streamStatusMessage.getRecipient()));
            fireIntent.putExtra(discriminatorKEY, streamStatusMessage.getCallbackType());
            fireIntent.putExtra(streamIdKEY, streamStatusMessage.streamId);
            fireIntent.putExtra(streamStatusKEY, streamStatusMessage.streamStatus);
            fireIntentToService(fireIntent);
        } catch (Exception e) {
            logger.error("Callback cannot be given to the services as there is some exception in the Firing the Intent", e);
        }
    }


    @Override
    public void onDiscoveryIncomingMessage(DiscoveryIncomingMessage discoveryIncomingMessage) {

        String serviceIdKEY = "service_id_tag";
        String discriminatorKEY = "discriminator";
        String discoveredServiceListKEY = "DiscoveredServices";
        String discoveryIdKEY = "DiscoveryId";
        Intent fireIntent = new Intent();
        try {
            fireIntent.putExtra(serviceIdKEY, gson.toJson(discoveryIncomingMessage.getRecipient()));
            fireIntent.putExtra(discriminatorKEY, discoveryIncomingMessage.getCallbackType());
            fireIntent.putExtra(discoveryIdKEY, discoveryIncomingMessage.discoveryId);
            fireIntent.putExtra(discoveredServiceListKEY, discoveryIncomingMessage.discoveredList);
            if (discoveryIncomingMessage.isSphereDiscovery) {
                fireIntentToSphere(fireIntent);
            } else {
                fireIntentToService(fireIntent);
            }
        } catch (Exception e) {
            logger.error("Callback cannot be given to the services as there is some exception in the Firing the Intent", e);
        }
    }

    public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {
        String serviceIdKEY = "service_id_tag";
        String discriminatorKEY = "discriminator";
        Intent fireIntent = new Intent();
        try {
            fireIntent.putExtra(serviceIdKEY, gson.toJson(pipeMsg.getRecipient()));
            fireIntent.putExtra(discriminatorKEY, pipeMsg.getCallbackType());
            fireIntent.putExtra(BezirkActions.KEY_PIPE, pipeMsg.getPipe().toJson());
            fireIntent.putExtra(BezirkActions.KEY_PIPE_REQ_ID, pipeMsg.getPipeReqId());
            fireIntent.putExtra(BezirkActions.KEY_PIPE_POLICY_IN, pipeMsg.getAllowedIn().toJson());
            fireIntent.putExtra(BezirkActions.KEY_PIPE_POLICY_OUT, pipeMsg.getAllowedOut().toJson());

            fireIntentToService(fireIntent);
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
