package com.bezirk.proxy;

import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SendUnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.pubsubbroker.PubSubBrokerServiceTrigger;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

public class ProxyServer {
    private PubSubBrokerServiceTrigger pubSubBrokerService;

    public void registerZirk(RegisterZirkAction registerZirkAction) {
        pubSubBrokerService.registerService(registerZirkAction.getZirkId(), registerZirkAction.getZirkName());
    }

    public void subscribeService(SubscriptionAction subscriptionAction) {
        pubSubBrokerService.subscribeService(subscriptionAction.getZirkId(), subscriptionAction.getRole());
    }

    public void sendMulticastEvent(SendMulticastEventAction eventAction) {
        pubSubBrokerService.sendMulticastEvent(eventAction.getZirkId(), eventAction.getRecipientSelector(),
                eventAction.getSerializedEvent(), eventAction.getTopic());
    }

    public void sendUnicastEvent(SendUnicastEventAction eventAction) {
        pubSubBrokerService.sendUnicastEvent(eventAction.getZirkId(), (BezirkZirkEndPoint) eventAction.getRecipient(),
                eventAction.getSerializedEvent(), eventAction.getTopic());
    }

    public short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, StreamDescriptor descriptor, File file, short streamId) {
        return pubSubBrokerService.sendStream(senderId, receiver, descriptor.toJson(), file, streamId);
    }

    public void setLocation(SetLocationAction locationAction) {
        pubSubBrokerService.setLocation(locationAction.getZirkId(), locationAction.getLocation());
    }

    public boolean unsubscribe(SubscriptionAction subscriptionAction) {

        return pubSubBrokerService.unsubscribe(subscriptionAction.getZirkId(), subscriptionAction.getRole());
    }

    public boolean unregister(SubscriptionAction subscriptionAction) {
        return pubSubBrokerService.unregisterService(subscriptionAction.getZirkId());
    }

    public void setPubSubBrokerService(PubSubBrokerServiceTrigger pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
