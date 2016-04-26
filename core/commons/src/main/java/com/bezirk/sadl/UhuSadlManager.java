package com.bezirk.sadl;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.IUhuComms;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.discovery.DiscoveryManager;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.persistence.ISadlPersistence;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuDiscoveredZirk;
import com.bezirk.proxy.api.impl.UhuZirkId;
import com.bezirk.remotelogging.messages.UhuLoggingMessage;
import com.bezirk.remotelogging.queues.LoggingQueueManager;
import com.bezirk.remotelogging.spherefilter.FilterLogMessages;
import com.bezirk.remotelogging.status.LoggingStatus;
import com.bezirk.remotelogging.util.Util;
import com.bezirk.rest.BezirkRestCallBack;
import com.bezirk.rest.BezirkRestCallBackImpl;
import com.bezirk.rest.BezirkRestCommsManager;
import com.bezirk.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the ISadlRegistry, ISadlRegistryLookup Interfaces. This class is used by ProxyForServices (by casting ISadlRegistry)
 * EventSender/ EventReceiver/ ControlSender/ ControlReceiver by casting ISadlRegistryLookup.
 */
public class UhuSadlManager implements ISadlRegistry, ISadlRegistryLookup, ISadlControlReceiver, ISadlEventReceiver {
    private static final Logger logger = LoggerFactory.getLogger(UhuSadlManager.class);

    private final Date currentDate = new Date();
    protected ISadlPersistence sadlPersistence = null;
    protected SadlRegistry sadlRegistry = null;
    protected IUhuComms uhuComms = null;

    public UhuSadlManager(ISadlPersistence sadlPersistence) {
        this.sadlPersistence = sadlPersistence;
        loadSadlRegistry();
    }

    /**
     * initialize the object references for future use
     */
    public void initSadlManager(IUhuComms uhuComms) {
        this.uhuComms = uhuComms;
        initServiceDiscovery(uhuComms);
    }

    /**
     * moved the init discovery from comms layer to sphere.
     * because this is out of comms layer
     */
    public void initServiceDiscovery(IUhuComms uhuComms) {
        DiscoveryManager discoveryManager = new DiscoveryManager(this, uhuComms);

        discoveryManager.initDiscovery();
    }

    /* (non-Javadoc)
     * @see com.bezirk.api.sadl.ISadlRegistry#registerZirk(com.bezirk.api.addressing.UhuZirkId)
     */
    @Override
    public Boolean registerService(final UhuZirkId serviceId) {
        if (!UhuValidatorUtility.checkUhuServiceId(serviceId)) {
            logger.error("Invalid UhuZirkId");
            return false;
        }
        if (isServiceRegisterd(serviceId)) {
            logger.info(serviceId + " Service is already registered");
            return false;
        }
        if (sadlRegistry.registerService(serviceId)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see ISadlRegistry#subscribeService(com.bezirk.api.addressing.UhuZirkId, ProtocolRole)
     */
    @Override
    public Boolean subscribeService(final UhuZirkId serviceId, final ProtocolRole pRole) {
        if (!UhuValidatorUtility.checkUhuServiceId(serviceId) || !UhuValidatorUtility.checkProtocolRole((SubscribedRole) pRole)) {
            logger.error("Invalid Subscription, Validation failed");
            return false;
        }
        if (!isServiceRegisterd(serviceId)) {
            logger.info("Service tried to subscribe without Registration");
            return false;
        }

        if (sadlRegistry.subscribeService(serviceId, pRole)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    @Override
    public Boolean unsubscribe(final UhuZirkId serviceId, final ProtocolRole role) {
        if (!UhuValidatorUtility.checkUhuServiceId(serviceId) || !UhuValidatorUtility.checkProtocolRole((SubscribedRole) role)) {
            logger.error("Invalid UnSubscription, Validation failed");
            return false;
        }
        if (sadlRegistry.unsubscribe(serviceId, role)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    @Override
    public Boolean unregisterService(final UhuZirkId serviceId) {
        if (!UhuValidatorUtility.checkUhuServiceId(serviceId)) {
            logger.error("Invalid UnRegistration, Validation failed");
            return false;
        }
        if (sadlRegistry.unregisterService(serviceId)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }


    @Override
    public Boolean setLocation(final UhuZirkId serviceId, final Location location) {
        if (sadlRegistry.setLocation(serviceId, location)) {
            persistSadlRegistry();
            return true;
        }
        return false;
    }

    @Override
    public Boolean isServiceRegisterd(UhuZirkId serviceId) {
        if (UhuValidatorUtility.checkUhuServiceId(serviceId)) {
            return sadlRegistry.isServiceRegisterd(serviceId);
        }
        return false;
    }

    @Override
    public Location getLocationForService(UhuZirkId serviceId) {
        return sadlRegistry.getLocationForService(serviceId);
    }


    @Override
    public Boolean isStreamTopicRegistered(String streamTopic, UhuZirkId serviceId) {
        if (!UhuValidatorUtility.checkForString(streamTopic) || !UhuValidatorUtility.checkUhuServiceId(serviceId)) {
            logger.error("Stream Topic or service Id is invalid");
            return false;
        }
        return sadlRegistry.isStreamTopicRegistered(streamTopic, serviceId);
    }

    // SERVICE-NAME NEEDS TO BE FILLED
    @Override
    public Set<UhuDiscoveredZirk> discoverServices(ProtocolRole pRole, Location location) {
        if (!UhuValidatorUtility.checkProtocolRole((SubscribedRole) pRole)) {
            logger.error("Discarding Discovery Lookup as ProtocolRole is invalid");
            return null;
        }
        return sadlRegistry.discoverServices(pRole, location);
    }


    @Override
    public boolean processEvent(final EventLedger eLedger) {

        Set<UhuZirkId> invokeList = fetchInvokeList(eLedger);

        if (null == invokeList || invokeList.isEmpty()) {
            logger.debug("No services are present to respond to the request");
            return false;
        }
        // FIXME: commented decrypt to test the comms-zyre-jni, enable it later
        if (!eLedger.getIsLocal() && !decryptMsg(eLedger)) {
            return false;
        }

        if (LoggingStatus.isLoggingEnabled() && FilterLogMessages.checkSphere(eLedger.getHeader().getSphereName())) {
            sendRemoteLogMessage(eLedger);
        }

        // give a callback to appropriate service..
        giveCallback(eLedger, invokeList);

        return true;
    }

    private void giveCallback(final EventLedger eLedger,
                              Set<UhuZirkId> invokeList) {
        // check if the service exists in that sphere then give callback
        for (UhuZirkId serviceId : invokeList) {
            if (invokeList.contains(new UhuZirkId("SPOOFED")) &&
                    eLedger.getHeader().getSphereName().equals(BezirkRestCommsManager.getInstance().getSlectedSphereName())) {
                //send the response to HTTPComms also..
                BezirkRestCallBack callBack = new BezirkRestCallBackImpl();
                callBack.callBackForResponse(eLedger);

            } else if (UhuCompManager.getSphereForSadl().isServiceInSphere(serviceId, eLedger.getHeader().getSphereName())) {
                EventIncomingMessage eCallbackMessage = new EventIncomingMessage(serviceId, eLedger.getHeader().getSenderSEP(),
                        eLedger.getSerializedMessage(), eLedger.getHeader().getTopic(), eLedger.getHeader().getUniqueMsgId());
                UhuCompManager.getplatformSpecificCallback().onIncomingEvent(eCallbackMessage);
            } else {
                logger.debug("Unknown Service ID!!!!!");
            }
        }
    }

    private Set<UhuZirkId> fetchInvokeList(final EventLedger eLedger) {
        Set<UhuZirkId> invokeList = null;
        if (eLedger.getIsMulticast()) {
            MulticastHeader mHeader = (MulticastHeader) eLedger.getHeader();
            Location targetLocation = mHeader.getAddress() == null ? null : mHeader.getAddress().getLocation();
            invokeList = this.checkMulticastEvent(mHeader.getTopic(), targetLocation);
        } else {
            UnicastHeader uHeader = (UnicastHeader) eLedger.getHeader();

            //here i can check for the spoofed event and bypass the sadl validation
            if (uHeader != null && uHeader.getRecipient().serviceId.getUhuEventId() != null && uHeader.getRecipient().serviceId.getUhuServiceId().equals("THIS-SERVICE-ID-IS-HTTP-SPOOFED")) {
                invokeList = new HashSet<UhuZirkId>();
                invokeList.add(new UhuZirkId("SPOOFED"));
            } else if (this.checkUnicastEvent(uHeader.getTopic(), uHeader.getRecipient().serviceId)) {
                invokeList = new HashSet<UhuZirkId>();
                invokeList.add(uHeader.getRecipient().serviceId);
            }
        }
        return invokeList;
    }

    /**
     * decrypt the event
     */
    private Boolean decryptMsg(EventLedger eLedger) {
        // Decrypt the event message
        final String decryptedEventMsg = UhuCompManager.getSphereForSadl().decryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getEncryptedMessage());
        if (!UhuValidatorUtility.checkForString(decryptedEventMsg)) {
            logger.debug("Header Decryption Failed: sphereId-" + eLedger.getHeader().getSphereName() + " may not exist");

            return false;
        }
        eLedger.setSerializedMessage(decryptedEventMsg);
        return true;
    }

    /**
     * route the events logging message to
     */
    private void sendRemoteLogMessage(EventLedger eLedger) {
        try {
            LoggingQueueManager.loadLogSenderQueue(new UhuLoggingMessage(eLedger.getHeader().getSphereName(),
                    String.valueOf(currentDate.getTime()), UhuCompManager.getUpaDevice().getDeviceName(),
                    Util.CONTROL_RECEIVER_VALUE, eLedger.getHeader().getUniqueMsgId(), eLedger.getHeader().getTopic(), Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION).serialize());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    public boolean checkUnicastEvent(String topic, UhuZirkId recipient) {
        if (!UhuValidatorUtility.checkForString(topic) || !UhuValidatorUtility.checkUhuServiceId(recipient)) {
            logger.error("Unicast Event Check failed -> topic or Recipient is not valid");
            return false;
        }
        return sadlRegistry.checkUnicastEvent(topic, recipient);
    }

    // Return a HashSet<UhuZirkId> by creating a new one otherwise the receiving components can modify it!

    public Set<UhuZirkId> checkMulticastEvent(String topic, Location location) {
        if (!UhuValidatorUtility.checkForString(topic)) {
            logger.error("Event Topic or Recipient is valid");
            return null;
        }
        return sadlRegistry.checkMulticastEvent(topic, location);
    }

    @Override
    public Set<UhuZirkId> getRegisteredServices() {
        return sadlRegistry.getRegisteredServices();
    }

    private void loadSadlRegistry() {
        try {
            sadlRegistry = sadlPersistence.loadSadlRegistry();
        } catch (Exception e) {
            logger.error("Error in loading sadl registry from persistence \n", e);
        }
    }

    @Override
    public boolean processStreamStatus(StreamStatusMessage streamStatusNotifciation) {
        UhuCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusNotifciation);
        return true;
    }

    @Override
    public boolean processNewStream(StreamIncomingMessage streamData) {
        UhuCompManager.getplatformSpecificCallback().onIncomingStream(streamData);
        return true;
    }

    private void persistSadlRegistry() {
        try {
            sadlPersistence.persistSadlRegistry();
        } catch (Exception e) {
            logger.error("Error in storing data \n", e);
        }
    }


}
