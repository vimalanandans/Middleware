package com.bezirk.pubsubbroker;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.Comms;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.pubsubbroker.discovery.DiscoveryManager;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.persistence.PubSubBrokerPersistence;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;

import com.bezirk.remotelogging.RemoteLog;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the PubSubBrokerServiceTrigger, PubSubBrokerServiceInfo Interfaces. This class is used by ProxyForServices (by casting PubSubBrokerServiceTrigger)
 * EventSender/ EventReceiver/ ControlSender/ ControlReceiver by casting PubSubBrokerServiceInfo.
 */
public class PubSubBroker implements PubSubBrokerServiceTrigger, PubSubBrokerServiceInfo, PubSubBrokerControlReceiver, PubSubEventReceiver {
    private static final Logger logger = LoggerFactory.getLogger(PubSubBroker.class);

    protected PubSubBrokerPersistence pubSubBrokerPersistence = null;
    protected PubSubBrokerRegistry pubSubBrokerRegistry = null;
    protected Comms comms = null;
    RemoteLog remoteLog = null;

    public PubSubBroker(PubSubBrokerPersistence pubSubBrokerPersistence) {
        this.pubSubBrokerPersistence = pubSubBrokerPersistence;
        loadSadlRegistry();
    }

    /**
     * initialize the object references for future use
     */
    public void initPubSubBroker(Comms comms) {
        this.comms = comms;
        initServiceDiscovery(comms);
    }

    /**
     * moved the init discovery from comms layer to sphere.
     * because this is out of comms layer
     */
    public void initServiceDiscovery(Comms comms) {
        DiscoveryManager discoveryManager = new DiscoveryManager(this, comms);

        discoveryManager.initDiscovery();
    }

    /* (non-Javadoc)
     * @see com.bezirk.api.sadl.PubSubBrokerServiceTrigger#registerZirk(com.bezirk.api.addressing.ZirkId)
     */
    @Override
    public Boolean registerService(final ZirkId serviceId) {
        if (!ValidatorUtility.checkBezirkZirkId(serviceId)) {
            logger.error("Invalid ZirkId");
            return false;
        }
        if (isServiceRegistered(serviceId)) {
            logger.info(serviceId + " Zirk is already registered");
            return false;
        }
        if (pubSubBrokerRegistry.registerService(serviceId)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see PubSubBrokerServiceTrigger#subscribeService(com.bezirk.api.addressing.ZirkId, ProtocolRole)
     */
    @Override
    public Boolean subscribeService(final ZirkId serviceId, final ProtocolRole pRole) {
        if (!ValidatorUtility.checkBezirkZirkId(serviceId) || !ValidatorUtility.checkProtocolRole((SubscribedRole) pRole)) {
            logger.error("Invalid Subscription, Validation failed");
            return false;
        }
        if (!isServiceRegistered(serviceId)) {
            logger.info("Zirk tried to subscribe without Registration");
            return false;
        }

        if (pubSubBrokerRegistry.subscribeService(serviceId, pRole)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    @Override
    public Boolean unsubscribe(final ZirkId serviceId, final ProtocolRole role) {
        if (!ValidatorUtility.checkBezirkZirkId(serviceId) || !ValidatorUtility.checkProtocolRole((SubscribedRole) role)) {
            logger.error("Invalid UnSubscription, Validation failed");
            return false;
        }
        if (pubSubBrokerRegistry.unsubscribe(serviceId, role)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    @Override
    public Boolean unregisterService(final ZirkId serviceId) {
        if (!ValidatorUtility.checkBezirkZirkId(serviceId)) {
            logger.error("Invalid UnRegistration, Validation failed");
            return false;
        }
        if (pubSubBrokerRegistry.unregisterZirk(serviceId)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }


    @Override
    public Boolean setLocation(final ZirkId serviceId, final Location location) {
        if (pubSubBrokerRegistry.setLocation(serviceId, location)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    @Override
    public Boolean isServiceRegistered(ZirkId serviceId) {
        if (ValidatorUtility.checkBezirkZirkId(serviceId)) {
            return pubSubBrokerRegistry.isServiceRegistered(serviceId);
        }
        return false;
    }

    @Override
    public Location getLocationForService(ZirkId serviceId) {
        return pubSubBrokerRegistry.getLocationForService(serviceId);
    }


    @Override
    public Boolean isStreamTopicRegistered(String streamTopic, ZirkId serviceId) {
        if (!ValidatorUtility.checkForString(streamTopic) || !ValidatorUtility.checkBezirkZirkId(serviceId)) {
            logger.error("Stream Topic or zirk Id is invalid");
            return false;
        }
        return pubSubBrokerRegistry.isStreamTopicRegistered(streamTopic, serviceId);
    }

    // SERVICE-NAME NEEDS TO BE FILLED
    @Override
    public Set<BezirkDiscoveredZirk> discoverZirks(ProtocolRole pRole, Location location) {
        if (!ValidatorUtility.checkProtocolRole((SubscribedRole) pRole)) {
            logger.error("Discarding Discovery Lookup as ProtocolRole is invalid");
            return null;
        }
        return pubSubBrokerRegistry.discoverServices(pRole, location);
    }


    @Override
    public boolean processEvent(final EventLedger eLedger) {

        Set<ZirkId> invokeList = fetchInvokeList(eLedger);

        if (null == invokeList || invokeList.isEmpty()) {
            logger.debug("No services are present to respond to the request");
            return false;
        }
        // FIXME: commented decrypt to test the comms-zyre-jni, enable it later
        if (!eLedger.getIsLocal() && !decryptMsg(eLedger)) {
            return false;
        }

        if((remoteLog != null) && remoteLog.isEnabled())
        {
            remoteLog.sendRemoteLogMessage(eLedger);
        }

        // give a callback to appropriate zirk..
        giveCallback(eLedger, invokeList);

        return true;
    }

    private void giveCallback(final EventLedger eLedger,
                              Set<ZirkId> invokeList) {
        // check if the zirk exists in that sphere then give callback
        for (ZirkId serviceId : invokeList) {
            /* rest comms disabled
            if (invokeList.contains(new ZirkId("SPOOFED")) &&
                    eLedger.getHeader().getSphereName().equals(BezirkRestCommsManager.getInstance().getSelectedSphereName())) {
                //send the response to HTTPComms also..
                BezirkRestCallBack callBack = new BezirkRestCallBackImpl();
                callBack.callBackForResponse(eLedger);

            } else */
            if (BezirkCompManager.getSphereForPubSubBroker().isZirkInSphere(serviceId, eLedger.getHeader().getSphereName())) {
                EventIncomingMessage eCallbackMessage = new EventIncomingMessage(serviceId, eLedger.getHeader().getSenderSEP(),
                        eLedger.getSerializedMessage(), eLedger.getHeader().getTopic(), eLedger.getHeader().getUniqueMsgId());
                BezirkCompManager.getplatformSpecificCallback().onIncomingEvent(eCallbackMessage);
            } else {
                logger.debug("Unknown Zirk ID!!!!!");
            }
        }
    }

    private Set<ZirkId> fetchInvokeList(final EventLedger eLedger) {
        Set<ZirkId> invokeList = null;
        if (eLedger.getIsMulticast()) {
            MulticastHeader mHeader = (MulticastHeader) eLedger.getHeader();
            Location targetLocation = mHeader.getRecipientSelector() == null ? null : mHeader.getRecipientSelector().getLocation();
            invokeList = this.checkMulticastEvent(mHeader.getTopic(), targetLocation);
        } else {
            UnicastHeader uHeader = (UnicastHeader) eLedger.getHeader();

            //here i can check for the spoofed event and bypass the sadl validation
            if (uHeader != null && uHeader.getRecipient().zirkId.getBezirkEventId() != null && uHeader.getRecipient().zirkId.getZirkId().equals("THIS-SERVICE-ID-IS-HTTP-SPOOFED")) {
                invokeList = new HashSet<ZirkId>();
                invokeList.add(new ZirkId("SPOOFED"));
            } else if (this.checkUnicastEvent(uHeader.getTopic(), uHeader.getRecipient().zirkId)) {
                invokeList = new HashSet<ZirkId>();
                invokeList.add(uHeader.getRecipient().zirkId);
            }
        }
        return invokeList;
    }

    /**
     * decrypt the event
     */
    private Boolean decryptMsg(EventLedger eLedger) {
        // Decrypt the event message
        final String decryptedEventMsg = BezirkCompManager.getSphereForPubSubBroker().decryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getEncryptedMessage());
        if (!ValidatorUtility.checkForString(decryptedEventMsg)) {
            logger.debug("Header Decryption Failed: sphereId-" + eLedger.getHeader().getSphereName() + " may not exist");

            return false;
        }
        eLedger.setSerializedMessage(decryptedEventMsg);
        return true;
    }



    public boolean checkUnicastEvent(String topic, ZirkId recipient) {
        if (!ValidatorUtility.checkForString(topic) || !ValidatorUtility.checkBezirkZirkId(recipient)) {
            logger.error("Unicast Event Check failed -> topic or Recipient is not valid");
            return false;
        }
        return pubSubBrokerRegistry.checkUnicastEvent(topic, recipient);
    }

    // Return a HashSet<ZirkId> by creating a new one otherwise the receiving components can modify it!

    public Set<ZirkId> checkMulticastEvent(String topic, Location location) {
        if (!ValidatorUtility.checkForString(topic)) {
            logger.error("Event Topic or Recipient is valid");
            return null;
        }
        return pubSubBrokerRegistry.checkMulticastEvent(topic, location);
    }

    @Override
    public Set<ZirkId> getRegisteredServices() {
        return pubSubBrokerRegistry.getRegisteredServices();
    }

    private void loadSadlRegistry() {
        try {
            pubSubBrokerRegistry = pubSubBrokerPersistence.loadPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in loading sadl registry from persistence \n", e);
        }
    }

    @Override
    public boolean processStreamStatus(StreamStatusMessage streamStatusNotification) {
        BezirkCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusNotification);
        return true;
    }

    @Override
    public boolean processNewStream(StreamIncomingMessage streamData) {
        BezirkCompManager.getplatformSpecificCallback().onIncomingStream(streamData);
        return true;
    }

    private void persistSadlRegistry() {
        try {
            pubSubBrokerPersistence.persistPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in storing data \n", e);
        }
    }


}
