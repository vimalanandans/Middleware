package com.bezirk.proxy;

import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.identity.IdentityProvisioner;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.pubsubbroker.PubSubBrokerZirkServicer;

import org.jetbrains.annotations.NotNull;

public class ProxyServer {
    private PubSubBrokerZirkServicer pubSubBrokerService;
    private final IdentityManager identityManager;

    public ProxyServer(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public void registerZirk(@NotNull RegisterZirkAction registerZirkAction) {
        pubSubBrokerService.registerZirk(registerZirkAction.getZirkId(), registerZirkAction.getZirkName());
    }

    public void subscribe(@NotNull SubscriptionAction subscriptionAction) {
        pubSubBrokerService.subscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public void sendEvent(@NotNull SendMulticastEventAction eventAction) {
        if (eventAction.isIdentified()) {
            eventAction.setAlias(((IdentityProvisioner) identityManager).getAlias());
        }

        pubSubBrokerService.sendMulticastEvent(eventAction);
    }

    public void sendEvent(@NotNull UnicastEventAction eventAction) {
        if (eventAction.isIdentified()) {
            eventAction.setAlias(((IdentityProvisioner) identityManager).getAlias());
        }

        pubSubBrokerService.sendUnicastEvent(eventAction);
    }

    public short sendStream(@NotNull SendFileStreamAction streamAction) {
        return pubSubBrokerService.sendStream(streamAction.getZirkId(),
                (BezirkZirkEndPoint) streamAction.getRecipient(), streamAction.getDescriptor().toJson(),
                streamAction.getFile());
    }

    public void setLocation(@NotNull SetLocationAction locationAction) {
        pubSubBrokerService.setLocation(locationAction.getZirkId(), locationAction.getLocation());
    }

    public boolean unsubscribe(@NotNull SubscriptionAction subscriptionAction) {
        return pubSubBrokerService.unsubscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public boolean unregister(@NotNull RegisterZirkAction registerZirkAction) {
        return pubSubBrokerService.unregisterZirk(registerZirkAction.getZirkId());
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setPubSubBrokerService(PubSubBrokerZirkServicer pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
