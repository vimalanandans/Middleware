package com.bezirk.proxy;

import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.pubsubbroker.PubSubBrokerServiceTrigger;

public class ProxyServer {
    private PubSubBrokerServiceTrigger pubSubBrokerService;

    public void registerZirk(RegisterZirkAction registerZirkAction) {
        pubSubBrokerService.registerService(registerZirkAction.getZirkId(), registerZirkAction.getZirkName());
    }

    public void subscribeService(SubscriptionAction subscriptionAction) {
        pubSubBrokerService.subscribeService(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public void sendMulticastEvent(SendMulticastEventAction eventAction) {
        pubSubBrokerService.sendMulticastEvent(eventAction.getZirkId(), eventAction.getRecipientSelector(),
                eventAction.getSerializedEvent());
    }

    public void sendUnicastEvent(UnicastEventAction eventAction) {
        pubSubBrokerService.sendUnicastEvent(eventAction.getZirkId(), (BezirkZirkEndPoint) eventAction.getEndpoint(),
                eventAction.getSerializedEvent());
    }

    public short sendStream(SendFileStreamAction streamAction) {
        return pubSubBrokerService.sendStream(streamAction.getZirkId(),
                (BezirkZirkEndPoint) streamAction.getRecipient(), streamAction.getDescriptor().toJson(),
                streamAction.getFile(), streamAction.getStreamId());
    }

    public void setLocation(SetLocationAction locationAction) {
        pubSubBrokerService.setLocation(locationAction.getZirkId(), locationAction.getLocation());
    }

    public boolean unsubscribe(SubscriptionAction subscriptionAction) {

        return pubSubBrokerService.unsubscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public boolean unregister(RegisterZirkAction registerZirkAction) {
        return pubSubBrokerService.unregisterService(registerZirkAction.getZirkId());
    }

    public void setPubSubBrokerService(PubSubBrokerServiceTrigger pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
