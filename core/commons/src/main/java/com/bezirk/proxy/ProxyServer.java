package com.bezirk.proxy;

import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.pubsubbroker.PubSubBrokerZirkServicer;

public class ProxyServer {
    private PubSubBrokerZirkServicer pubSubBrokerService;
    private final IdentityManager identityManager;

    public ProxyServer(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public void registerZirk(RegisterZirkAction registerZirkAction) {
        pubSubBrokerService.registerZirk(registerZirkAction.getZirkId(), registerZirkAction.getZirkName());
    }

    public void subscribeService(SubscriptionAction subscriptionAction) {
        pubSubBrokerService.subscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public void sendMulticastEvent(SendMulticastEventAction eventAction) {
        pubSubBrokerService.sendMulticastEvent(eventAction.getZirkId(), eventAction.getRecipientSelector(),
                eventAction.getSerializedEvent(),eventAction.getEventName());
    }

    public void sendUnicastEvent(UnicastEventAction eventAction) {
        pubSubBrokerService.sendUnicastEvent(eventAction.getZirkId(), (BezirkZirkEndPoint) eventAction.getEndpoint(),
                eventAction.getSerializedEvent(),eventAction.getEventName());
    }

    public short sendStream(SendFileStreamAction streamAction) {
        return pubSubBrokerService.sendStream(streamAction.getZirkId(),
                (BezirkZirkEndPoint) streamAction.getRecipient(), streamAction.getDescriptor().toJson(),
                streamAction.getFile());
    }

    public void setLocation(SetLocationAction locationAction) {
        pubSubBrokerService.setLocation(locationAction.getZirkId(), locationAction.getLocation());
    }

    public boolean unsubscribe(SubscriptionAction subscriptionAction) {
        return pubSubBrokerService.unsubscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public boolean unregister(RegisterZirkAction registerZirkAction) {
        return pubSubBrokerService.unregisterZirk(registerZirkAction.getZirkId());
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setPubSubBrokerService(PubSubBrokerZirkServicer pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
