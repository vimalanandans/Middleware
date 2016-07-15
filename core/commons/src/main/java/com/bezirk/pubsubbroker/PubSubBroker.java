package com.bezirk.pubsubbroker;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.GenerateMsgId;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.pubsubbroker.discovery.DiscoveryLabel;
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

import com.bezirk.pubsubbroker.discovery.DiscoveryProcessor;
import com.bezirk.pubsubbroker.discovery.DiscoveryRecord;
import com.bezirk.remotelogging.RemoteLog;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.ValidatorUtility;
import com.bezrik.network.BezirkNetworkUtilities;
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
     * @see com.bezirk.api.sadl.PubSubBrokerServiceTrigger#registerService(com.bezirk.api.addressing.ZirkId)
     */
    @Override
    public Boolean registerService(final ZirkId serviceId, final String serviceName) {

        // Step 1: Register with PubSubBroker
        boolean isPubSubPassed = registerService(serviceId );

        if (isPubSubPassed) {
            // Step 2: moved to outside since the sphere persistence is not ready
        } else {
            logger.error("PubSubBroker Registration failed, Zirk ID is already registered");
        }

        // Step 2: Register with sphere
        boolean isSpherePassed = BezirkCompManager.getSphereForPubSubBroker().registerService(serviceId, serviceName);

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
    public boolean sendMulticastEvent(ZirkId serviceId, RecipientSelector recipientSelector, String serializedEventMsg) {

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForPubSubBroker().getSphereMembership(serviceId);

        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + serviceId.getZirkId());
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        final StringBuilder eventTopic = new StringBuilder((Event.fromJson(serializedEventMsg, Event.class)).topic);

        while (sphereIterator.hasNext()) {

            final EventLedger ecMessage = new EventLedger();
            ecMessage.setIsMulticast(true);
            ecMessage.setSerializedMessage(serializedEventMsg);
            ecMessage.setIsLocal(true);
            final MulticastHeader mHeader = new MulticastHeader();
            mHeader.setRecipientSelector(recipientSelector);
            mHeader.setSenderSEP(senderSEP);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setTopic(eventTopic.toString());
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
    public boolean sendUnicastEvent(ZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg){
        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForPubSubBroker().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with the sphere");
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        final StringBuilder eventTopic = new StringBuilder((Event.fromJson(serializedEventMsg, Event.class)).topic);

        while (sphereIterator.hasNext()) {
            final EventLedger ecMessage = new EventLedger();
            ecMessage.setIsMulticast(false);
            ecMessage.setSerializedMessage(serializedEventMsg);
            ecMessage.setIsLocal(recipient.device.equals(senderSEP.device));
            final UnicastHeader uHeader = new UnicastHeader();
            uHeader.setRecipient(recipient);
            uHeader.setSenderSEP(senderSEP);
            uHeader.setUniqueMsgId(uniqueMsgId.toString());
            uHeader.setTopic(eventTopic.toString());
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

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForPubSubBroker().getSphereMembership(senderId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + senderId);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(senderId);
            final String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getZirkId() + ":" + streamId;
            final Stream stream = new Gson().fromJson(serializedString, Stream.class);

            final StreamRecord streamRecord = prepareStreamRecord(receiver, serializedString, file, streamId, senderSEP, stream);

            boolean streamStoreStatus = comms.registerStreamBook(streamRequestKey, streamRecord);
            if (!streamStoreStatus) {
                logger.error("Cannot Register Stream, CtrlMsgId is already present in StreamBook");
                return (short) -1;
            }
            sendStreamToSpheres(sphereIterator, streamRequestKey, streamRecord, file, comms);
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }


    StreamRecord prepareStreamRecord(BezirkZirkEndPoint receiver, String serializedStream, File file, short streamId, BezirkZirkEndPoint senderSEP, Stream stream) {
        final StreamRecord streamRecord = new StreamRecord();
        streamRecord.localStreamId = streamId;
        streamRecord.senderSEP = senderSEP;
        streamRecord.isReliable = false;
        streamRecord.isIncremental = false;
        streamRecord.isEncrypted = stream.isEncrypted();
        streamRecord.sphere = null;
        streamRecord.streamStatus = StreamRecord.StreamingStatus.PENDING;
        streamRecord.recipientIP = receiver.device;
        streamRecord.recipientPort = 0;
        streamRecord.file = file;
        streamRecord.pipedInputStream = null;
        streamRecord.recipientSEP = receiver;
        streamRecord.serializedStream = serializedStream;
        streamRecord.streamTopic = stream.topic;
        return streamRecord;
    }

    String getSphereId(BezirkZirkEndPoint receiver, Iterator<String> sphereIterator) {
        String sphereId = null;
        while (sphereIterator.hasNext()) {
            sphereId = sphereIterator.next();
            if (BezirkCompManager.getSphereForPubSubBroker().isServiceInSphere(receiver.getBezirkZirkId(), sphereId)) {
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
        BezirkZirkEndPoint senderSEP = streamRecord.senderSEP;
        BezirkZirkEndPoint receiver = streamRecord.recipientSEP;
        String serializedStream = streamRecord.serializedStream;
        String streamTopic = streamRecord.streamTopic;
        short streamId = streamRecord.localStreamId;
        final StreamRequest request = new StreamRequest(senderSEP, receiver, sphereName, streamRequestKey, null, serializedStream, streamTopic, tempFile.getName(),
                streamRecord.isEncrypted, streamRecord.isIncremental, streamRecord.isReliable, streamId);
        tcMessage.setSphereId(sphereName);
        tcMessage.setMessage(request);
        tcMessage.setSerializedMessage(new Gson().toJson(request));

        return tcMessage;
    }

    public boolean discover(final ZirkId serviceId, final RecipientSelector recipientSelector, final SubscribedRole pRole,
                         final int discoveryId, final long timeout, final int maxDiscovered) {

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForPubSubBroker().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with the sphere");
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final Location loc = (recipientSelector == null) ? null : recipientSelector.getLocation();

        while (sphereIterator.hasNext()) {
            final ControlLedger ControlLedger = new ControlLedger();
            final String tempSphereName = sphereIterator.next();
            final DiscoveryRequest discoveryRequest = new DiscoveryRequest(tempSphereName, senderSEP, loc, pRole, discoveryId, timeout, maxDiscovered);
            ControlLedger.setSphereId(tempSphereName);
            ControlLedger.setMessage(discoveryRequest);
            ControlLedger.setSerializedMessage(ControlLedger.getMessage().serialize());
            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ControlLedger);
            } else {
                logger.error("Comms manager not initialized");
                return false;
            }
        }
        final DiscoveryLabel discoveryLabel = new DiscoveryLabel(senderSEP, discoveryId);
        final DiscoveryRecord pendingRequest = new DiscoveryRecord(timeout, maxDiscovered);
        DiscoveryProcessor.getDiscovery().addRequest(discoveryLabel, pendingRequest);

        return true;
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

        Set<ZirkId> serviceList = getAssociatedServiceList(eLedger);

        if (null == serviceList || serviceList.isEmpty()) {
            logger.debug("No services are present to respond to the request");
            return false;
        }

        if (!eLedger.getIsLocal() && !decryptMsg(eLedger)) {
            return false;
        }

        if((remoteLog != null) && remoteLog.isEnabled())
        {
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
            /* rest comms disabled
            if (invokeList.contains(new ZirkId("SPOOFED")) &&
                    eLedger.getHeader().getSphereName().equals(BezirkRestCommsManager.getInstance().getSelectedSphereName())) {
                //send the response to HTTPComms also..
                BezirkRestCallBack callBack = new BezirkRestCallBackImpl();
                callBack.callBackForResponse(eLedger);

            } else */
            if (BezirkCompManager.getSphereForPubSubBroker().isServiceInSphere(serviceId, eLedger.getHeader().getSphereName())) {
                EventIncomingMessage eCallbackMessage = new EventIncomingMessage(serviceId, eLedger.getHeader().getSenderSEP(),
                        eLedger.getSerializedMessage(), eLedger.getHeader().getTopic(), eLedger.getHeader().getUniqueMsgId());
                BezirkCompManager.getplatformSpecificCallback().onIncomingEvent(eCallbackMessage);
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
            } else*/ if (this.checkUnicastEvent(uHeader.getTopic(), uHeader.getRecipient().zirkId)) {
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
        final String decryptedEventMsg = BezirkCompManager.getSphereSecurity().
                decryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getEncryptedMessage());
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
