package com.bezirk.pubsubbroker;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.comms.Comms;
import com.bezirk.comms.processor.EventMsgReceiver;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.GenerateMsgId;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.datastorage.PubSubBrokerStorage;
import com.bezirk.device.Device;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.MessageHandler;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
//import com.bezirk.remotelogging.RemoteLog;
//import com.bezirk.remotelogging.RemoteLoggingManager;
import com.bezirk.remotelogging.RemoteLog;
import com.bezirk.remotelogging.RemoteLoggingMessage;
import com.bezirk.remotelogging.RemoteLoggingMessageNotification;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.streaming.Streaming;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
    private  NetworkManager networkManager=null;
    private  Device device=null;
    RemoteLog remoteLog;
    RemoteLoggingMessage remoteLoggingMessage;
    RemoteLoggingMessageNotification remoteLoggingMessageNotification;
    Streaming streamManger;
    MessageHandler msgHandler;
    private boolean checkEnableValueForAllSphere = false;

    public PubSubBroker(PubSubBrokerStorage pubSubBrokerStorage, Device device, NetworkManager networkManager, Comms comms, MessageHandler msgHandler,
                        SphereServiceAccess sphereServiceAccess, SphereSecurity sphereSecurity, Streaming streamManger,RemoteLog remoteLogging,RemoteLoggingMessage remoteLoggingMessage,RemoteLoggingMessageNotification remoteLoggingMessageNotification) {
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
        this.remoteLog = remoteLogging;
        this.remoteLoggingMessage= remoteLoggingMessage;
        this.remoteLoggingMessageNotification = remoteLoggingMessageNotification;

        this.streamManger = streamManger;

        if(streamManger != null) {
            streamManger.setEventReceiver(this);
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
        logger.debug("zirkId in register service is "+zirkId);
        if (!ValidatorUtility.checkBezirkZirkId(zirkId)) {
            logger.error("Invalid ZirkId");
            return false;
        }
        if (isZirkRegistered(zirkId)) {
            logger.info(zirkId + " Zirk is already registered");
            return false;
        }
        if (pubSubBrokerRegistry.registerZirk(zirkId)) {
            logger.debug("pubSubBrokerRegistry.registerZirk(zirkId)");
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
    public boolean sendMulticastEvent(SendMulticastEventAction multicastEventAction) {
        logger.debug("sendMulticastEvent method in PubSubBroker");
        final ZirkId zirkId = multicastEventAction.getZirkId();
        logger.debug("zirk id is "+zirkId.getZirkId());
        final Iterable<String> listOfSphere;

        if (sphereServiceAccess != null) {
            logger.debug("sphereServiceAccess is not null");
            listOfSphere = sphereServiceAccess.getSphereMembership(zirkId);
        } else {
            logger.debug("sphereServiceAccess is  null");
            Set<String> spheres = new HashSet<>();
            spheres.add(SPHERE_NULL_NAME);
            listOfSphere = spheres;
        }

        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + zirkId.getZirkId());
            return false;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint sender = networkManager.getServiceEndPoint(zirkId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(sender));


        while (sphereIterator.hasNext()) {

            logger.debug("Iterating over sphere");
            final EventLedger eventLedger = new EventLedger();
            logger.debug("multicastEventAction.getSerializedEvent() is "+multicastEventAction.getSerializedEvent());
            eventLedger.setSerializedMessage(multicastEventAction.getSerializedEvent());

            final MulticastHeader mHeader = new MulticastHeader();
            mHeader.setRecipientSelector(multicastEventAction.getRecipientSelector());
            mHeader.setSender(sender);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setSphereId(sphereIterator.next());
            mHeader.setEventName(multicastEventAction.getEventName());
            mHeader.setIsIdentified(multicastEventAction.isIdentified());

            if (multicastEventAction.isIdentified()) {
                logger.debug("(multicastEventAction.isIdentified() is true in PubSubbroker");
                mHeader.setAlias(multicastEventAction.getAlias());
            }
            if(null!=mHeader){
                logger.debug("mHeader is not null");
                eventLedger.setHeader(mHeader);
            }
            else{
                logger.debug("mHeader is null");
            }
            eventLedger.setIsMulticast(true);
            eventLedger.setSerializedHeader(mHeader.serialize());

            if (ValidatorUtility.isObjectNotNull(comms)) {
                logger.debug("comms not null in PubSubBroker class");
                comms.sendEventLedger(eventLedger);

            } else {
                logger.error("Comms manager not initialized");
                return false;
            }
            logger.debug("before calling sendMessageToLocal method and pass param eventLedger....");
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
        final BezirkZirkEndPoint sender = networkManager.getServiceEndPoint(zirkId);
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
        logger.debug("in method sendMessageToLocal");
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

/*<<<<<<< HEAD
    void sendStreamToSpheres(Iterator<String> sphereIterator, String streamRequestKey, StreamRecord streamRecord, File tempFile, Comms comms) {
        while (sphereIterator.hasNext()) {
            final ControlLedger tcMessage = prepareMessage(sphereIterator, streamRequestKey, streamRecord, tempFile);
            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendControlLedger(tcMessage);
                //sendMessage Deprecated
                //comms.sendMessage(tcMessage);
            } else {
                logger.error("Comms manager not initialized");
=======*/
            //boolean status = comms.processStreamRecord(streamAction,listOfSphere);
            boolean status = streamManger.processStreamRecord(streamAction, listOfSphere);
            if (!status) {
                return (short) 1;
//>>>>>>> midw_mvp
            }
        }else{
            logger.error("Streaming manager is not initialized!!!");
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
        logger.debug("processEvent method");
        Set<ZirkId> zirkList = getAssociatedZirkList(eLedger);

        if (null == zirkList || zirkList.isEmpty()) {

            //logger.debug("No services are present to respond to the request");
            return false;
        }

        if (!decryptMsg(eLedger)) {
            return false;
        }
        if(null!=remoteLog){
            logger.debug("remoteLog is not null in PubSubBroker");
            checkEnableValueForAllSphere =  remoteLog.enableRemoteLoggingForAllSpheres();
            logger.debug("checkEnableForAllSphere in PubSubBroker is "+checkEnableValueForAllSphere);
            if(checkEnableValueForAllSphere){
                boolean remoteLogging = remoteLog.sendRemoteLogLedgerMessage(eLedger);
                logger.debug("remoteLogging is "+remoteLogging);

                try {
                    logger.debug("start logging");
                    remoteLoggingMessageNotification.handleLogMessage(remoteLoggingMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //}
        //}else{
          //  logger.debug("remoteLog is  null in PubSubBroker");
        }
        /*if ((remoteLog != null) && remoteLog.isRemoteLoggingEnabled()) {
            remoteLog.sendRemoteLogLedgerMessage(eLedger);
        }*/

        // give a callback to appropriate zirk..
        triggerMessageHandler(eLedger, zirkList);

        return true;
    }

    private void triggerMessageHandler(final EventLedger eLedger,
                                       Set<ZirkId> invokeList) {
        // check if the zirk exists in that sphere then give callback
        logger.debug("triggerMessageHandler method call");
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
