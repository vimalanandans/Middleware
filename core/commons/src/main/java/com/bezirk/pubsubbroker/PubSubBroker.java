package com.bezirk.pubsubbroker;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.comms.Comms;
import com.bezirk.comms.processor.EventMsgReceiver;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.GenerateMsgId;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.datastorage.PubSubBrokerStorage;
import com.bezirk.device.Device;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.MessageHandler;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.remotelogging.RemoteLog;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.ValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements the PubSubBrokerZirkServicer, PubSubBrokerServiceInfo Interfaces. This class is used by ProxyForServices (by casting PubSubBrokerZirkServicer)
 * EventSender/ EventReceiver/ ControlSender/ ControlReceiver by casting PubSubBrokerServiceInfo.
 */
public class PubSubBroker implements PubSubBrokerZirkServicer, PubSubBrokerServiceInfo, PubSubBrokerControlReceiver, PubSubEventReceiver, EventMsgReceiver {
    private static final Logger logger = LoggerFactory.getLogger(PubSubBroker.class);

    private static final String SPHERE_NULL_NAME = "SPHERE_NONE";

    protected PubSubBrokerStorage pubSubBrokerStorage = null;
    protected PubSubBrokerRegistry pubSubBrokerRegistry = null;
    protected Comms comms = null;
    protected SphereServiceAccess sphereServiceAccess = null; // Nullable object
    protected SphereSecurity sphereSecurity = null; // Nullable object
    private final NetworkManager networkManager;
    private final Device device;
    RemoteLog remoteLog = null;

    MessageHandler msgHandler;

    public PubSubBroker(PubSubBrokerStorage pubSubBrokerStorage, Device device, NetworkManager networkManager, Comms comms, MessageHandler msgHandler,
                        SphereServiceAccess sphereServiceAccess, SphereSecurity sphereSecurity) {
        this.pubSubBrokerStorage = pubSubBrokerStorage;
        this.device = device;
        this.networkManager = networkManager;
        loadRegistry();

        this.comms = comms;
        // register event processor
        this.comms.registerEventMessageReceiver(this);
        this.sphereServiceAccess = sphereServiceAccess;
        this.sphereSecurity = sphereSecurity;
        this.msgHandler = msgHandler;
    }


    /**
     * initialize the object references for future use
     */
//    public void initPubSubBroker(Comms comms, MessageHandler msgHandler,
//                                 SphereServiceAccess sphereServiceAccess, SphereSecurity sphereSecurity) {
//        this.comms = comms;
//        // register event processor
//        comms.registerEventMessageReceiver(this);
//        this.sphereServiceAccess = sphereServiceAccess;
//        this.sphereSecurity = sphereSecurity;
//        this.msgHandler = msgHandler;
//
//    }

    /* (non-Javadoc)
     * @see com.bezirk.api.sadl.PubSubBrokerZirkServicer#registerZirk(com.bezirk.api.addressing.ZirkId)
     */
    @Override
    public boolean registerZirk(final ZirkId zirkId, final String zirkName) {

        // Step 1: Register with PubSubBroker
        boolean isPubSubPassed = registerService(zirkId);

        if (isPubSubPassed) {
            // Step 2: moved to outside since the sphere persistence is not ready
        } else {
            logger.error("PubSubBroker Registration failed, Zirk ID is already registered");
        }

        if (sphereServiceAccess != null) {
            // Step 2: Register with sphere
            boolean isSpherePassed = sphereServiceAccess.registerService(zirkId, zirkName);

            if (isSpherePassed) {
                logger.info("Zirk Registration Complete for: {}, {}", zirkName, zirkId);
            } else {
                // unregister the PubSubBroker due to failure in sphere
                logger.error("sphere Registration Failed. unregistring PubSubBroker");
                unregisterZirk(zirkId);
            }
        }
        return isPubSubPassed;
    }


    public Boolean registerService(final ZirkId serviceId) {
        if (!ValidatorUtility.checkBezirkZirkId(serviceId)) {
            logger.error("Invalid ZirkId");
            return false;
        }
        if (isZirkRegistered(serviceId)) {
            logger.info(serviceId + " Zirk is already registered");
            return false;
        }
        if (pubSubBrokerRegistry.registerZirk(serviceId)) {
            persistRegistry();
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see PubSubBrokerZirkServicer#subscribe(com.bezirk.api.addressing.ZirkId, MessageSet)
     */
    @Override
    public boolean subscribe(final ZirkId zirkId, final MessageSet messageSet) {
        if (!isZirkRegistered(zirkId)) {
            logger.info("Zirk tried to subscribe without Registration");
            return false;
        }

        if (pubSubBrokerRegistry.subscribe(zirkId, messageSet)) {
            persistRegistry();
            return true;
        }
        return false;
    }

    @Override
    public boolean unsubscribe(final ZirkId zirkId, final MessageSet messageSet) {
        if (pubSubBrokerRegistry.unsubscribe(zirkId, messageSet)) {
            persistRegistry();
            return true;
        }
        return false;
    }

    @Override
    public boolean unregisterZirk(final ZirkId zirkId) {
        if (!ValidatorUtility.checkBezirkZirkId(zirkId)) {
            logger.error("Invalid UnRegistration, Validation failed");
            return false;
        }
        if (pubSubBrokerRegistry.unregisterZirk(zirkId)) {
            persistRegistry();
            return true;
        }
        return false;
    }


    @Override
    public boolean setLocation(final ZirkId zirkId, final Location location) {
        if (pubSubBrokerRegistry.setLocation(zirkId, location)) {
            persistRegistry();
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMulticastEvent(ZirkId zirkId, RecipientSelector recipientSelector, String serializedEventMsg, String eventName ) {

        final Iterable<String> listOfSphere;

        if (sphereServiceAccess != null) {
            listOfSphere = sphereServiceAccess.getSphereMembership(zirkId);
        } else {
            Set<String> spheres = new HashSet<>();
            spheres.add(SPHERE_NULL_NAME);
            listOfSphere = spheres;
        }

        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + zirkId.getZirkId());
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = networkManager.getServiceEndPoint(zirkId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));


        while (sphereIterator.hasNext()) {

            final EventLedger eventLedger = new EventLedger();
            eventLedger.setIsMulticast(true);
            eventLedger.setSerializedMessage(serializedEventMsg);
            final MulticastHeader mHeader = new MulticastHeader();
            mHeader.setRecipientSelector(recipientSelector);
            mHeader.setSenderSEP(senderSEP);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setSphereName(sphereIterator.next());
            mHeader.setEventName(eventName);
            eventLedger.setHeader(mHeader);
            eventLedger.setSerializedHeader(mHeader.serialize());

            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(eventLedger);
            } else {
                logger.error("Comms manager not initialized");
                return false;
            }

            sendMessageToLocal(eventLedger);

        }
        return true;
    }

    @Override
    public boolean sendUnicastEvent(ZirkId zirkId, BezirkZirkEndPoint recipient, String serializedEventMsg, String eventName) {
        final Iterable<String> listOfSphere;

        if (sphereServiceAccess != null) {
            listOfSphere = sphereServiceAccess.getSphereMembership(zirkId);
        } else {
            Set<String> spheres = new HashSet<>();
            spheres.add(SPHERE_NULL_NAME);
            listOfSphere = spheres;
        }

        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with the sphere");
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = networkManager.getServiceEndPoint(zirkId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        //final StringBuilder eventTopic = new StringBuilder((Event.fromJson(serializedEventMsg, Event.class)).topic);

        while (sphereIterator.hasNext()) {
            final EventLedger eventLedger = new EventLedger();
            eventLedger.setIsMulticast(false);
            eventLedger.setSerializedMessage(serializedEventMsg);
            final UnicastHeader uHeader = new UnicastHeader();
            uHeader.setRecipient(recipient);
            uHeader.setSenderSEP(senderSEP);
            uHeader.setUniqueMsgId(uniqueMsgId.toString());
            uHeader.setSphereName(sphereIterator.next());
            uHeader.setEventName(eventName);
            eventLedger.setHeader(uHeader);
            eventLedger.setSerializedHeader(uHeader.serialize());

            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(eventLedger);
            } else {
                logger.error("Comms manager not initialized");
                return false;
            }

            sendMessageToLocal(eventLedger);
        }
        return true;
    }


    /** send the event messages to local zirks */
    void sendMessageToLocal(EventLedger eventLedger)
    {
        processEvent(eventLedger);
    }


    public short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file) {


        final Iterable<String> listOfSphere;

        if (sphereServiceAccess != null) {
            listOfSphere = sphereServiceAccess.getSphereMembership(senderId);
        } else {
            Set<String> spheres = new HashSet<>();
            spheres.add(SPHERE_NULL_NAME);
            listOfSphere = spheres;
        }

        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + senderId);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            final BezirkZirkEndPoint senderSEP = networkManager.getServiceEndPoint(senderId);
            final String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getZirkId();
            final StreamDescriptor streamDescriptor = new Gson().fromJson(serializedString, StreamDescriptor.class);

            final StreamRecord streamRecord = prepareStreamRecord(receiver, serializedString, file, senderSEP, streamDescriptor);

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


    StreamRecord prepareStreamRecord(BezirkZirkEndPoint receiver, String serializedStream, File file,
                                     BezirkZirkEndPoint senderSEP, StreamDescriptor streamDescriptor) {
        final StreamRecord streamRecord = new StreamRecord();
        streamRecord.senderSEP = senderSEP;
        streamRecord.isReliable = false;
        streamRecord.isIncremental = false;
        streamRecord.isEncrypted = streamDescriptor.isEncrypted();
        streamRecord.sphere = null;
        streamRecord.streamStatus = StreamRecord.StreamingStatus.PENDING;
        streamRecord.recipientIP = receiver.device;
        streamRecord.recipientPort = 0;
        streamRecord.file = file;
        streamRecord.pipedInputStream = null;
        streamRecord.recipientSEP = receiver;
        streamRecord.serializedStream = serializedStream;
        return streamRecord;
    }

//    String getSphereId(BezirkZirkEndPoint receiver, Iterator<String> sphereIterator) {
//
//        String sphereId = null;
//
//        while (sphereIterator.hasNext()) {
//
//            sphereId = sphereIterator.next();
//
//            if (sphereServiceAccess != null && // valid object, but no service
//                    sphereServiceAccess.isServiceInSphere(receiver.getBezirkZirkId(), sphereId)) {
//                logger.debug("Found the sphere:" + sphereId);
//                break;
//            } else { // not valid sphere object return the first one
//                break;
//            }
//        }
//        return sphereId;
//    }

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
        final StreamRequest request = new StreamRequest(senderSEP, receiver, sphereName, streamRequestKey, null, serializedStream, tempFile.getName(),
                streamRecord.isEncrypted, streamRecord.isIncremental, streamRecord.isReliable);
        tcMessage.setSphereId(sphereName);
        tcMessage.setMessage(request);
        tcMessage.setSerializedMessage(new Gson().toJson(request));

        return tcMessage;
    }

    @Override
    public Boolean isZirkRegistered(ZirkId zirkId) {
        if (ValidatorUtility.checkBezirkZirkId(zirkId)) {
            return pubSubBrokerRegistry.isZirkRegistered(zirkId);
        }
        return false;
    }

    @Override
    public Location getLocationForService(ZirkId serviceId) {
        return pubSubBrokerRegistry.getLocationForZirk(serviceId, device);
    }


    @Override
    public boolean isStreamTopicRegistered(String streamName, ZirkId serviceId) {
        return pubSubBrokerRegistry.isStreamTopicRegistered(streamName, serviceId);
    }

    /** called on incoming message and loop back message*/
    @Override
    public boolean processEvent(final EventLedger eLedger) {

        Set<ZirkId> serviceList = getAssociatedServiceList(eLedger);

        if (null == serviceList || serviceList.isEmpty()) {
            //logger.debug("No services are present to respond to the request");
            return false;
        }

        if (!decryptMsg(eLedger)) {
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

            if (isServiceInSphere(serviceId, eLedger.getHeader().getSphereName())) {
                UnicastEventAction eventMessage = new UnicastEventAction(BezirkAction.ACTION_ZIRK_RECEIVE_EVENT,
                        serviceId, eLedger.getHeader().getSenderSEP(), eLedger.getSerializedMessage(),
                        eLedger.getHeader().getUniqueMsgId(),eLedger.getHeader().getEventName() );
                msgHandler.onIncomingEvent(eventMessage);
            } else {
                logger.debug("Unknown Zirk ID!!!!!");
            }
        }
    }

    // true - valid service in sphere
    private boolean isServiceInSphere(ZirkId service, String sphereId) {
        return sphereServiceAccess == null || sphereServiceAccess.isServiceInSphere(service, sphereId);
    }

    private Set<ZirkId> getAssociatedServiceList(final EventLedger eLedger) {

        Set<ZirkId> serviceList = null;

        if (eLedger.getIsMulticast()) {
            MulticastHeader mHeader = (MulticastHeader) eLedger.getHeader();
            Location targetLocation = mHeader.getRecipientSelector() == null ? null : mHeader.getRecipientSelector().getLocation();
            serviceList = this.checkMulticastEvent(mHeader.getEventName(), targetLocation);
        } else {
            UnicastHeader uHeader = (UnicastHeader) eLedger.getHeader();
            if (this.checkUnicastEvent(uHeader.getEventName(), uHeader.getRecipient().zirkId)) {
                serviceList = new HashSet<>();
                serviceList.add(uHeader.getRecipient().zirkId);
            }
        }
        return serviceList;
    }

    /**
     * decrypt the event
     */
    private Boolean decryptMsg(EventLedger eLedger) {
        final String decryptedEventMsg;

        if (sphereSecurity != null) {
            // Decrypt the event message
            decryptedEventMsg = sphereSecurity.decryptSphereContent(
                    eLedger.getHeader().getSphereName(), eLedger.getEncryptedMessage());

            if (!ValidatorUtility.checkForString(decryptedEventMsg)) {
                logger.debug("Decryption Failed: sphereId-" + eLedger.getHeader().getSphereName() + " may not exist");
                return false;
            }

        } else { // no sphere object hence
            if(eLedger.getEncryptedMessage() == null) //if it is local message
                decryptedEventMsg = eLedger.getSerializedMessage();
            else
                decryptedEventMsg = new String(eLedger.getEncryptedMessage());
        }




        eLedger.setSerializedMessage(decryptedEventMsg);
        return true;
    }


    public boolean checkUnicastEvent(String eventName, ZirkId recipient) {
        if (!ValidatorUtility.checkBezirkZirkId(recipient)) {
            logger.error("Unicast Event Check failed -> Recipient is not valid");
            return false;
        }
        return pubSubBrokerRegistry.checkUnicastEvent(eventName, recipient);
    }

    // Return a HashSet<ZirkId> by creating a new one otherwise the receiving components can modify it!
    public Set<ZirkId> checkMulticastEvent(String eventName, Location location) {
        return pubSubBrokerRegistry.checkMulticastEvent(eventName, location, device);
    }

    @Override
    public Set<ZirkId> getRegisteredZirks() {
        return pubSubBrokerRegistry.getRegisteredZirks();
    }

    private void loadRegistry() {
        try {
            pubSubBrokerRegistry = pubSubBrokerStorage.loadPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in loading registry from persistence \n", e);
        }
    }

   /* @Override
    public boolean processStreamStatus(StreamStatusAction streamStatusNotification) {
        msgHandler.onStreamStatus(streamStatusNotification);
        return true;
    }*/

    @Override
    public boolean processNewStream(ReceiveFileStreamAction streamData) {
        msgHandler.onIncomingStream(streamData);
        return true;
    }

    private void persistRegistry() {
        try {
            pubSubBrokerStorage.persistPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in storing data \n", e);
        }
    }


}
