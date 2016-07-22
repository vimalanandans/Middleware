package com.bezirk.proxy;

import com.bezirk.actions.SendEventAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SendUnicastEventAction;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.pubsubbroker.PubSubBrokerServiceTrigger;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Bridges the main module / Main service to pubsub broker.
 * TODO : Do we really need this module. This has become just an adapter for PubSubBroker
 */
public class ProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);

    private PubSubBrokerServiceTrigger pubSubBrokerService;

    public void registerZirk(final ZirkId serviceId, final String serviceName) {
        pubSubBrokerService.registerService(serviceId,serviceName);
    }

    public void subscribeService(final ZirkId serviceId, final ProtocolRole pRole) {
        pubSubBrokerService.subscribeService(serviceId, pRole);
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

    public void setLocation(final ZirkId zirkId, final Location location) {
        pubSubBrokerService.setLocation(zirkId, location);
    }

    public boolean unsubscribe(final ZirkId zirkId, final ProtocolRole role) {

        return pubSubBrokerService.unsubscribe(zirkId, role);
    }

    public boolean unregister(ZirkId zirkId) {
        return pubSubBrokerService.unregisterService(zirkId);
    }

    public void setPubSubBrokerService(PubSubBrokerServiceTrigger pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
