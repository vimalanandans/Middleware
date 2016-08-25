package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.ReceiveFileStreamAction;
import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.GenerateMsgId;
import com.bezirk.middleware.core.control.messages.MulticastHeader;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.core.datastorage.PubSubBrokerStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.core.remotelogging.RemoteLog;
import com.bezirk.middleware.core.sphere.api.SphereSecurity;
import com.bezirk.middleware.core.sphere.api.SphereServiceAccess;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.core.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
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
    Streaming streamManger;

    MessageHandler msgHandler;

    public PubSubBroker(PubSubBrokerStorage pubSubBrokerStorage, Device device, NetworkManager networkManager, Comms comms, MessageHandler msgHandler,
                        SphereServiceAccess sphereServiceAccess, SphereSecurity sphereSecurity, Streaming streamManger) {
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
        this.streamManger = streamManger;

        if(streamManger != null) {
            streamManger.setEventReceiver(this);

            //Initialize the Streaming module
            streamManger.startStreams();
        }
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


    public Boolean registerService(final ZirkId zirkId) {
        if (!ValidatorUtility.checkBezirkZirkId(zirkId)) {
            logger.error("Invalid ZirkId");
            return false;
        }
        if (isZirkRegistered(zirkId)) {
            logger.info(zirkId + " Zirk is already registered");
            return false;
        }
        if (pubSubBrokerRegistry.registerZirk(zirkId)) {
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

    public boolean sendMulticastEvent(SendMulticastEventAction multicastEventAction) {
        final ZirkId zirkId = multicastEventAction.getZirkId();
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
        final BezirkZirkEndPoint sender ;

        if(comms != null)
            sender = new BezirkZirkEndPoint(comms.getNodeId(),zirkId);
        else
            sender = networkManager.getServiceEndPoint(zirkId);
        // sender = new BezirkZirkEndPoint(zirkId);

        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(sender));


        while (sphereIterator.hasNext()) {

            final EventLedger eventLedger = new EventLedger();
            eventLedger.setSerializedMessage(multicastEventAction.getSerializedEvent());
            final MulticastHeader mHeader = new MulticastHeader();
            mHeader.setRecipientSelector(multicastEventAction.getRecipientSelector());
            mHeader.setSender(sender);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setSphereId(sphereIterator.next());
            mHeader.setEventName(multicastEventAction.getEventName());
            mHeader.setIsIdentified(multicastEventAction.isIdentified());

            if (multicastEventAction.isIdentified()) {
                mHeader.setAlias(multicastEventAction.getAlias());
            }

            eventLedger.setHeader(mHeader);
            eventLedger.setIsMulticast(true);
            eventLedger.setSerializedHeader(mHeader.serialize());

            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendEventLedger(eventLedger);
            } else {
                logger.error("Comms manager not initialized");
                return false;
            }

            sendMessageToLocal(eventLedger);

        }

        return true;
    }

    @Override
    public boolean sendUnicastEvent(UnicastEventAction unicastEventAction) {
        final ZirkId zirkId = unicastEventAction.getZirkId();
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
        final BezirkZirkEndPoint sender;

        if(comms != null)
            sender = new BezirkZirkEndPoint(comms.getNodeId(),zirkId);
        else
            sender = networkManager.getServiceEndPoint(zirkId);
           // sender = new BezirkZirkEndPoint(zirkId);

        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(sender));
        //final StringBuilder eventTopic = new StringBuilder((Event.fromJson(serializedEventMsg, Event.class)).topic);

        while (sphereIterator.hasNext()) {
            final EventLedger eventLedger = new EventLedger();

            eventLedger.setSerializedMessage(unicastEventAction.getSerializedEvent());
            final UnicastHeader uHeader = new UnicastHeader();
            uHeader.setRecipient((BezirkZirkEndPoint) unicastEventAction.getEndpoint());
            uHeader.setSender(sender);
            uHeader.setUniqueMsgId(uniqueMsgId.toString());
            uHeader.setSphereId(sphereIterator.next());
            uHeader.setEventName(unicastEventAction.getEventName());
            uHeader.setIsIdentified(unicastEventAction.isIdentified());

            if (unicastEventAction.isIdentified()) {
                uHeader.setAlias(unicastEventAction.getAlias());
            }

            eventLedger.setHeader(uHeader);
            eventLedger.setIsMulticast(false);
            eventLedger.setSerializedHeader(uHeader.serialize());

            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendEventLedger(eventLedger);
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


    /**
     * sends the stream request to comms module and then to streaming module.
     * @param streamAction
     * @return
     */
    public short sendStream(SendFileStreamAction streamAction) {
        final Iterable<String> listOfSphere;

        if(streamManger != null) {
            //know the spheres the zirk belongs to. We will send the stream control message to all registered spheres
            if (sphereServiceAccess != null) {
                listOfSphere = sphereServiceAccess.getSphereMembership(streamAction.getZirkId());
            } else {
                Set<String> spheres = new HashSet<>();
                spheres.add(SPHERE_NULL_NAME);
                listOfSphere = spheres;
            }

            if (null == listOfSphere) {
                logger.error("Zirk Not Registered with any sphere: " + streamAction.getZirkId());
                return (short) -1;
            }

            /*
            * process the stream record which will
            *store the streamrecord in the stream store and sends the stream message to receivers.*/

            //boolean status = comms.processStreamRecord(streamAction,listOfSphere);
            boolean status = streamManger.processStreamRecord(streamAction, listOfSphere);
            if (!status) {
                return (short) 1;
            }
        }else{
            logger.error("Streaming manager is not initialized!!!");
            return (short) -1;
        }

        return (short) 1;
    }


    @Override
    public Boolean isZirkRegistered(ZirkId zirkId) {
        if (ValidatorUtility.checkBezirkZirkId(zirkId)) {
            return pubSubBrokerRegistry.isZirkRegistered(zirkId);
        }
        return false;
    }

    @Override
    public Location getLocationForZirk(ZirkId zirkId) {
        return pubSubBrokerRegistry.getLocationForZirk(zirkId, device);
    }


    @Override
    public boolean isStreamTopicRegistered(String streamName, ZirkId zirkId) {
        return pubSubBrokerRegistry.isStreamTopicRegistered(streamName, zirkId);
    }

    /** called on incoming message and loop back message*/
    @Override
    public boolean processEvent(final EventLedger eLedger) {
        Set<ZirkId> zirkList = getAssociatedZirkList(eLedger);

        if (null == zirkList || zirkList.isEmpty()) {
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
        triggerMessageHandler(eLedger, zirkList);

        return true;
    }

    private void triggerMessageHandler(final EventLedger eLedger,
                                       Set<ZirkId> invokeList) {
        // check if the zirk exists in that sphere then give callback
        for (ZirkId zirkId : invokeList) {

            if (isServiceInSphere(zirkId, eLedger.getHeader().getSphereId())) {
                UnicastEventAction eventMessage = new UnicastEventAction(BezirkAction.ACTION_ZIRK_RECEIVE_EVENT,
                        zirkId, eLedger.getHeader().getSender(), eLedger.getSerializedMessage(),
                        eLedger.getHeader().getUniqueMsgId(),eLedger.getHeader().getEventName(),
                        eLedger.getHeader().isIdentified());

                if (eLedger.getHeader().isIdentified()) {
                    eventMessage.setAlias(eLedger.getHeader().getAlias());
                }

                msgHandler.onIncomingEvent(eventMessage);
            } else {
                logger.debug("Unknown Zirk ID!!!!!");
            }
        }
    }

    // true - valid zirk in sphere
    private boolean isServiceInSphere(ZirkId zirkId, String sphereId) {
        return sphereServiceAccess == null || sphereServiceAccess.isServiceInSphere(zirkId, sphereId);
    }

    private Set<ZirkId> getAssociatedZirkList(final EventLedger eLedger) {

        Set<ZirkId> zirkList = null;

        if (eLedger.getHeader() instanceof MulticastHeader) {
            MulticastHeader mHeader = (MulticastHeader) eLedger.getHeader();
            Location targetLocation = mHeader.getRecipientSelector() == null ? null : mHeader.getRecipientSelector().getLocation();
            zirkList = this.checkMulticastEvent(mHeader.getEventName(), targetLocation);
        } else {
            UnicastHeader uHeader = (UnicastHeader) eLedger.getHeader();
            if (this.checkUnicastEvent(uHeader.getEventName(), uHeader.getRecipient().zirkId)) {
                zirkList = new HashSet<>();
                zirkList.add(uHeader.getRecipient().zirkId);
            }
        }
        return zirkList;
    }

    /**
     * decrypt the event
     */
    private Boolean decryptMsg(EventLedger eLedger) {
        final String decryptedEventMsg;

        if (sphereSecurity != null) {
            // Decrypt the event message
            decryptedEventMsg = sphereSecurity.decryptSphereContent(
                    eLedger.getHeader().getSphereId(), eLedger.getEncryptedMessage());

            if (!ValidatorUtility.checkForString(decryptedEventMsg)) {
                logger.debug("Decryption Failed: sphereId-" + eLedger.getHeader().getSphereId() + " may not exist");
                return false;
            }

        } else { // no sphere object hence
            if(eLedger.getEncryptedMessage() == null) //if it is local message
                decryptedEventMsg = eLedger.getSerializedMessage();
            else {
                try {
                    decryptedEventMsg = new String(eLedger.getEncryptedMessage(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
                }
            }
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
