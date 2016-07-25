package com.bezirk.proxy.android;

import android.content.Intent;

import com.bezirk.actions.BezirkActions;
import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SendUnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.ProxyServer;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AndroidProxyServer extends ProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(AndroidProxyServer.class);

    private static final Gson gson = new Gson();
    private MessageHandler messageHandler;

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void registerZirk(Intent intent) {
        final RegisterZirkAction registrationAction = (RegisterZirkAction) intent.getSerializableExtra(BezirkActions.ACTION_BEZIRK_REGISTER.getName());

        logger.debug("Zirk registration received by Bezirk. Name: {}, ID: {}",
                registrationAction.getZirkName(), registrationAction.getZirkId());

        super.registerZirk(registrationAction);
    }

    public void subscribeService(Intent intent) {
        logger.trace("Received subscription from zirk");

        final SubscriptionAction subscriptionAction = (SubscriptionAction) intent.getSerializableExtra(BezirkActions.ACTION_BEZIRK_SUBSCRIBE.getName());

        super.subscribeService(subscriptionAction);
    }

    public void unsubscribeService(Intent intent) {
        logger.trace("Received unsubscribe from zirk");

        final SubscriptionAction subscriptionAction = (SubscriptionAction) intent.getSerializableExtra(BezirkActions.ACTION_BEZIRK_UNSUBSCRIBE.getName());

        if (subscriptionAction.getRole() != null) {
            super.unsubscribe(subscriptionAction);
        } else {
            super.unregister(new RegisterZirkAction(subscriptionAction.getZirkId(), null));
        }
    }

    public void sendMulticastEvent(Intent intent) {
        logger.trace("Received multicast message from zirk");

        SendMulticastEventAction eventAction = (SendMulticastEventAction) intent.getSerializableExtra(BezirkActions.ACTION_SERVICE_SEND_MULTICAST_EVENT.getName());

        super.sendMulticastEvent(eventAction);
    }

    public void sendUnicastEvent(Intent intent) {
        logger.trace("Received unicast message from zirk");

        SendUnicastEventAction eventAction = (SendUnicastEventAction) intent.getSerializableExtra(BezirkActions.ACTION_SERVICE_SEND_UNICAST_EVENT.getName());

        super.sendUnicastEvent(eventAction);
    }

    public void sendUnicastStream(Intent intent) {
        logger.trace("Stream to unicast from Zirk");

        SendFileStreamAction streamAction = (SendFileStreamAction) intent.getSerializableExtra(BezirkActions.ACTION_SERVICE_SEND_UNICAST_EVENT.getName());

        short sendStreamStatus = super.sendStream(streamAction);

        if (sendStreamStatus != -1) {
            StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(
                    gson.fromJson(streamAction.getDescriptor().toJson(), ZirkId.class),
                    0, streamAction.getStreamId());
            messageHandler.onStreamStatus(streamStatusCallbackMessage);
        }
    }

    public void setLocation(Intent intent) {
        SetLocationAction locationAction = (SetLocationAction) intent.getSerializableExtra(BezirkActions.ACTION_BEZIRK_SET_LOCATION.getName());

        logger.trace("Received location {} from zirk", locationAction.getLocation());

        super.setLocation(locationAction);
    }
}