package com.bezirk.pubsubbroker;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.GenerateMsgId;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.datastorage.PubSubBrokerStorage;
import com.bezirk.devices.DeviceInterface;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.ZirkId;

import com.bezirk.remotelogging.RemoteLog;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.ValidatorUtility;
import com.bezrik.network.NetworkUtilities;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements the PubSubBrokerServiceTrigger, PubSubBrokerServiceInfo Interfaces. This class is used by ProxyForServices (by casting PubSubBrokerServiceTrigger)
 * EventSender/ EventReceiver/ ControlSender/ ControlReceiver by casting PubSubBrokerServiceInfo.
 */
public class PubSubBroker implements PubSubBrokerServiceTrigger, PubSubBrokerServiceInfo, PubSubBrokerControlReceiver, PubSubEventReceiver {
    private static final Logger logger = LoggerFactory.getLogger(PubSubBroker.class);

    protected PubSubBrokerStorage pubSubBrokerStorage = null;
    protected PubSubBrokerRegistry pubSubBrokerRegistry = null;
    protected Comms comms = null;
    protected SphereServiceAccess sphereServiceAccess = null;
    protected SphereSecurity sphereSecurity = null;
    DeviceInterface deviceInterface = null;
    RemoteLog remoteLog = null;

    MessageHandler msgHandler;

    public PubSubBroker(PubSubBrokerStorage pubSubBrokerStorage, DeviceInterface deviceInterface) {
        this.pubSubBrokerStorage = pubSubBrokerStorage;
        this.deviceInterface = deviceInterface;
        loadSadlRegistry();
    }


    /**
     * initialize the object references for future use
     */
    public void initPubSubBroker(Comms comms, MessageHandler msgHandler,
                                 SphereServiceAccess sphereServiceAccess, SphereSecurity sphereSecurity) {
        this.comms = comms;
        this.sphereServiceAccess = sphereServiceAccess;
        this.sphereSecurity = sphereSecurity;
        this.msgHandler = msgHandler;

    }

    /* (non-Javadoc)
     * @see com.bezirk.api.sadl.PubSubBrokerServiceTrigger#registerZirk(com.bezirk.api.addressing.ZirkId)
     */
    @Override
    public Boolean registerService(final ZirkId serviceId, final String serviceName) {

        // Step 1: Register with PubSubBroker
        boolean isPubSubPassed = registerService(serviceId);

        if (isPubSubPassed) {
            // Step 2: moved to outside since the sphere persistence is not ready
        } else {
            logger.error("PubSubBroker Registration failed, Zirk ID is already registered");
        }

        // Step 2: Register with sphere
        boolean isSpherePassed = sphereServiceAccess.registerService(serviceId, serviceName);

        if (isSpherePassed) {
            logger.info("Zirk Registration Complete for: {}, {}", serviceName, serviceId);
        } else {
            // unregister the PubSubBroker due to failure in sphere
            logger.error("sphere Registration Failed. unregistring PubSubBroker");
            unregisterService(serviceId);
        }
        return isPubSubPassed;
    }

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
    public boolean sendMulticastEvent(ZirkId serviceId, RecipientSelector recipientSelector, String serializedEventMsg, String topic) {

        final Iterable<String> listOfSphere = sphereServiceAccess.getSphereMembership(serviceId);

        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + serviceId.getZirkId());
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = NetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        //final StringBuilder eventTopic = new StringBuilder((Event.fromJson(serializedEventMsg, Event.class)).topic);

        while (sphereIterator.hasNext()) {

            final EventLedger ecMessage = new EventLedger();
            ecMessage.setIsMulticast(true);
            ecMessage.setSerializedMessage(serializedEventMsg);
            ecMessage.setIsLocal(true);
            final MulticastHeader mHeader = new MulticastHeader();
            mHeader.setRecipientSelector(recipientSelector);
            mHeader.setSenderSEP(senderSEP);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setTopic(topic);
            mHeader.setSphereName(sphereIterator.next());
            ecMessage.setHeader(mHeader);
            ecMessage.setSerializedHeader(mHeader.serialize());

            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                logger.error("Comms manager not initialized");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sendUnicastEvent(ZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg, String topic) {
        final Iterable<String> listOfSphere = sphereServiceAccess.getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with the sphere");
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = NetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        //final StringBuilder eventTopic = new StringBuilder((Event.fromJson(serializedEventMsg, Event.class)).topic);

        while (sphereIterator.hasNext()) {
            final EventLedger ecMessage = new EventLedger();
            ecMessage.setIsMulticast(false);
            ecMessage.setSerializedMessage(serializedEventMsg);
            ecMessage.setIsLocal(recipient.device.equals(senderSEP.device));
            final UnicastHeader uHeader = new UnicastHeader();
            uHeader.setRecipient(recipient);
            uHeader.setSenderSEP(senderSEP);
            uHeader.setUniqueMsgId(uniqueMsgId.toString());
            uHeader.setTopic(topic);
            uHeader.setSphereName(sphereIterator.next());
            ecMessage.setHeader(uHeader);
            ecMessage.setSerializedHeader(uHeader.serialize());
            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                logger.error("Comms manager not initialized");
                return false;
            }
        }
        return true;
    }

    public short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId) {

        final Iterable<String> listOfSphere = sphereServiceAccess.getSphereMembership(senderId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + senderId);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            final BezirkZirkEndPoint senderSEP = NetworkUtilities.getServiceEndPoint(senderId);
            final String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getZirkId() + ":" + streamId;
            final StreamDescriptor streamDescriptor = new Gson().fromJson(serializedString, StreamDescriptor.class);

            final StreamRecord streamRecord = prepareStreamRecord(receiver, serializedString, file, streamId, senderSEP, streamDescriptor);

            boolean streamStoreStatus = comms.registerStreamBook(streamRequestKey, streamRecord);
            if (!streamStoreStatus) {
                logger.error("Cannot Register StreamDescriptor, CtrlMsgId is already present in StreamBook");
                return (short) -1;
            }
            sendStreamToSpheres(sphereIterator, streamRequestKey, streamRecord, file, comms);
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }


    StreamRecord prepareStreamRecord(BezirkZirkEndPoint receiver, String serializedStream, File file, short streamId, BezirkZirkEndPoint senderSEP, StreamDescriptor streamDescriptor) {
        final StreamRecord streamRecord = new StreamRecord();
        streamRecord.setLocalStreamId(streamId);
        streamRecord.setSenderSEP(senderSEP);
        //streamRecord.setReliable(false);
        //streamRecord.setIncremental(false);
        streamRecord.setEncryptedStream(streamDescriptor.isEncrypted());
        streamRecord.setSphere(null);
        streamRecord.setStreamStatus(StreamRecord.StreamingStatus.PENDING);
        streamRecord.setRecipientIP(receiver.device);
        streamRecord.setRecipientPort(0);
        streamRecord.setFile(file);
        //streamRecord.setPipedInputStream(null);
        streamRecord.setRecipientSEP(receiver);
        streamRecord.setSerializedStream(serializedStream);
        streamRecord.setStreamTopic(streamDescriptor.topic);
        return streamRecord;
    }

    String getSphereId(BezirkZirkEndPoint receiver, Iterator<String> sphereIterator) {
        String sphereId = null;
        while (sphereIterator.hasNext()) {
            sphereId = sphereIterator.next();
            if (sphereServiceAccess.isServiceInSphere(receiver.getBezirkZirkId(), sphereId)) {
                logger.debug("Found the sphere:" + sphereId);
                break;
            }
        }
        return sphereId;
    }

    void sendStreamToSpheres(Iterator<String> sphereIterator, String streamRequestKey, StreamRecord streamRecord, File tempFile, Comms comms) {
        while (sphereIterator.hasNext()) {
            final ControlLedger tcMessage = prepareMessage(sphereIterator, streamRequestKey, streamRecord, tempFile);
            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(tcMessage);
            } else {
                logger.error("Comms manager not initialized");
            }
        }
    }


    private ControlLedger prepareMessage(Iterator<String> sphereIterator, String streamRequestKey, StreamRecord streamRecord, File tempFile) {

        final String sphereName = sphereIterator.next();
        final ControlLedger tcMessage = new ControlLedger();
        tcMessage.setSphereId(sphereName);
        BezirkZirkEndPoint senderSEP = streamRecord.getSenderSEP();
        BezirkZirkEndPoint receiver = streamRecord.getRecipientSEP();
        String serializedStream = streamRecord.getSerializedStream();
        String streamTopic = streamRecord.getStreamTopic();
        short streamId = streamRecord.getLocalStreamId();
        final StreamRequest request = new StreamRequest(senderSEP, receiver, sphereName, streamRequestKey, null, serializedStream, streamTopic, tempFile.getName(),
                streamRecord.isEncryptedStream(), streamId);
        tcMessage.setSphereId(sphereName);
        tcMessage.setMessage(request);
        tcMessage.setSerializedMessage(new Gson().toJson(request));

        return tcMessage;
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
        return pubSubBrokerRegistry.getLocationForService(serviceId, deviceInterface);
    }


    @Override
    public Boolean isStreamTopicRegistered(String streamTopic, ZirkId serviceId) {
        if (!ValidatorUtility.checkForString(streamTopic) || !ValidatorUtility.checkBezirkZirkId(serviceId)) {
            logger.error("StreamDescriptor Topic or zirk Id is invalid");
            return false;
        }
        return pubSubBrokerRegistry.isStreamTopicRegistered(streamTopic, serviceId);
    }

    @Override
    public boolean processEvent(final EventLedger eLedger) {

        Set<ZirkId> serviceList = getAssociatedServiceList(eLedger);

        if (null == serviceList || serviceList.isEmpty()) {
            logger.debug("No services are present to respond to the request");
            return false;
        }

        if (!eLedger.getIsLocal() && !decryptMsg(eLedger)) {
            return false;
        }

        if ((remoteLog != null) && remoteLog.isEnabled()) {
            remoteLog.sendRemoteLogMessage(eLedger);
        }

        // give a callback to appropriate zirk..
        triggerMessageHandler(eLedger, serviceList);

        return true;
    }

    private void triggerMessageHandler(final EventLedger eLedger,
                                       Set<ZirkId> invokeList) {
        // check if the zirk exists in that sphere then give callback
        for (ZirkId serviceId : invokeList) {
            if (sphereServiceAccess.isServiceInSphere(serviceId, eLedger.getHeader().getSphereName())) {
                EventIncomingMessage eCallbackMessage = new EventIncomingMessage(serviceId, eLedger.getHeader().getSenderSEP(),
                        eLedger.getSerializedMessage(), eLedger.getHeader().getTopic(), eLedger.getHeader().getUniqueMsgId());
                msgHandler.onIncomingEvent(eCallbackMessage);
            } else {
                logger.debug("Unknown Zirk ID!!!!!");
            }
        }
    }

    private Set<ZirkId> getAssociatedServiceList(final EventLedger eLedger) {
        Set<ZirkId> serviceList = null;
        if (eLedger.getIsMulticast()) {
            MulticastHeader mHeader = (MulticastHeader) eLedger.getHeader();
            Location targetLocation = mHeader.getRecipientSelector() == null ? null : mHeader.getRecipientSelector().getLocation();
            serviceList = this.checkMulticastEvent(mHeader.getTopic(), targetLocation);
        } else {
            UnicastHeader uHeader = (UnicastHeader) eLedger.getHeader();

            //here i can check for the spoofed event and bypass the sadl validation
           /* if (uHeader != null && uHeader.getRecipient().zirkId.getBezirkEventId() != null
                    && uHeader.getRecipient().zirkId.getZirkId().equals("THIS-SERVICE-ID-IS-HTTP-SPOOFED")) {
                serviceList = new HashSet<ZirkId>();
                serviceList.add(new ZirkId("SPOOFED"));
            } else*/
            if (this.checkUnicastEvent(uHeader.getTopic(), uHeader.getRecipient().zirkId)) {
                serviceList = new HashSet<ZirkId>();
                serviceList.add(uHeader.getRecipient().zirkId);
            }
        }
        return serviceList;
    }

    /**
     * decrypt the event
     */
    private Boolean decryptMsg(EventLedger eLedger) {
        // Decrypt the event message
        final String decryptedEventMsg = sphereSecurity.decryptSphereContent(
                eLedger.getHeader().getSphereName(), eLedger.getEncryptedMessage());
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
        return pubSubBrokerRegistry.checkMulticastEvent(topic, location,deviceInterface);
    }

    @Override
    public Set<ZirkId> getRegisteredServices() {
        return pubSubBrokerRegistry.getRegisteredServices();
    }

    private void loadSadlRegistry() {
        try {
            pubSubBrokerRegistry = pubSubBrokerStorage.loadPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in loading sadl registry from persistence \n", e);
        }
    }

    @Override
    public boolean processStreamStatus(StreamStatusMessage streamStatusNotification) {
        msgHandler.onStreamStatus(streamStatusNotification);
        return true;
    }

    @Override
    public boolean processNewStream(StreamIncomingMessage streamData) {
        msgHandler.onIncomingStream(streamData);
        return true;
    }

    private void persistSadlRegistry() {
        try {
            pubSubBrokerStorage.persistPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in storing data \n", e);
        }
    }


}
