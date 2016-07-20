package com.bezirk.proxy;

import com.bezirk.pubsubbroker.PubSubBrokerServiceTrigger;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Bridges the main module / Main service to pubsub broker. 
 * TODO : Do we really need this module. This has become just an adopter for PubSubBroker
 * */
public class ProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);

    private PubSubBrokerServiceTrigger pubSubBrokerService;

    public void registerService(final ZirkId serviceId, final String serviceName) {

    }


    public void subscribeService(final ZirkId serviceId, final SubscribedRole pRole) {
        pubSubBrokerService.subscribeService(serviceId, pRole);
    }


    public void sendMulticastEvent(final ZirkId serviceId, final RecipientSelector recipientSelector, final String serializedEventMsg) {
        pubSubBrokerService.sendMulticastEvent(serviceId, recipientSelector, serializedEventMsg);
    }

    public void sendUnicastEvent(final ZirkId serviceId, final BezirkZirkEndPoint recipient, final String serializedEventMsg) {
        pubSubBrokerService.sendUnicastEvent(serviceId, recipient, serializedEventMsg );
    }


    public short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId) {
        return pubSubBrokerService.sendStream(senderId,receiver,serializedString,file,streamId);
    }


    @Deprecated
    public short sendStream(ZirkId sender, BezirkZirkEndPoint receiver, String serializedString, short streamId) {
        return (short) 1;
    }


    public void setLocation(final ZirkId zirkId, final Location location) {
        pubSubBrokerService.setLocation(zirkId, location);
    }


    public boolean unsubscribe(final ZirkId zirkId, final SubscribedRole role) {

        return pubSubBrokerService.unsubscribe(zirkId, role);
    }


    public boolean unregister(ZirkId zirkId) {

        return pubSubBrokerService.unregisterService(zirkId);
    }

    public void setPubSubBrokerService(PubSubBrokerServiceTrigger pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
