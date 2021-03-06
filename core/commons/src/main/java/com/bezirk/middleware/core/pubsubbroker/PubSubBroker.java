/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.comms.processor.WireMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.GenerateMsgId;
import com.bezirk.middleware.core.control.messages.MulticastHeader;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.core.datastorage.PubSubBrokerStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.remotelogging.RemoteLog;
import com.bezirk.middleware.core.sphere.api.SphereSecurity;
import com.bezirk.middleware.core.sphere.api.SphereServiceAccess;
import com.bezirk.middleware.core.util.ValidatorUtility;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * This class implements the PubSubBrokerZirkServicer, PubSubBrokerServiceInfo Interfaces. This class is
 * used by ProxyForServices (by casting PubSubBrokerZirkServicer)
 * EventSender/ EventReceiver/ ControlSender/ ControlReceiver by casting PubSubBrokerServiceInfo.
 */
public class PubSubBroker implements PubSubBrokerZirkServicer, PubSubBrokerServiceInfo,
        PubSubBrokerControlReceiver, EventMsgReceiver {
    public static final String SPHERE_NULL_NAME = "SPHERE_NONE";
    private static final Logger logger = LoggerFactory.getLogger(PubSubBroker.class);
    private static final String ID = UUID.randomUUID().toString();
    private final RemoteLog remoteLog;
    private final MessageHandler msgHandler;
    private final IdentityManager identityManager;
    protected Comms comms;
    private PubSubBrokerStorage pubSubBrokerStorage;
    private PubSubBrokerRegistry pubSubBrokerRegistry;
    // Nullable object
    private SphereServiceAccess sphereServiceAccess;
    // Nullable object
    private SphereSecurity sphereSecurity;
    private Device device;

    public PubSubBroker(PubSubBrokerStorage pubSubBrokerStorage, Device device,
                        Comms comms, MessageHandler msgHandler, IdentityManager identityManager,
                        SphereServiceAccess sphereServiceAccess, SphereSecurity sphereSecurity,
                        RemoteLog remoteLogging) {
        this.pubSubBrokerStorage = pubSubBrokerStorage;
        this.device = device;
        loadRegistry();

        this.comms = comms;
        // register event processor
        if (this.comms != null) {
            this.comms.registerEventMessageReceiver(this);
        }
        this.identityManager = identityManager;
        this.sphereServiceAccess = sphereServiceAccess;
        this.sphereSecurity = sphereSecurity;
        this.msgHandler = msgHandler;
        this.remoteLog = remoteLogging;
    }

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
            logger.info("PubSubBroker: Zirk ID is already registered");
        }

        if (sphereServiceAccess != null) {
            // Step 2: Register with sphere
            boolean isSpherePassed = sphereServiceAccess.registerService(zirkId, zirkName);

            if (isSpherePassed) {
                logger.info("Zirk Registration Complete for: {}, {}", zirkName, zirkId);
            } else {
                // unregister the PubSubBroker due to failure in sphere
                logger.error("sphere Registration Failed. unregistering PubSubBroker");
                unregisterZirk(zirkId);
            }
        }
        return isPubSubPassed;
    }


    private Boolean registerService(final ZirkId zirkId) {
        if (ValidatorUtility.checkBezirkZirkId(zirkId)) {
            if (isZirkRegistered(zirkId)) {
                logger.info(zirkId + " Zirk is already registered");
                return false;
            }

            if (pubSubBrokerRegistry.registerZirk(zirkId)) {
                persistRegistry();
                return true;
            }
        } else {
            logger.error("Invalid ZirkId passed to registerService");
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
    public boolean sendMulticastEvent(@NotNull SendMulticastEventAction multicastEventAction) {
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
        final BezirkZirkEndPoint sender;

        if (comms != null) {
            sender = new BezirkZirkEndPoint(comms.getNodeId(), zirkId);
        } else {
            sender = new BezirkZirkEndPoint(ID, zirkId);
        }

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

            if (comms != null) {
                comms.sendEventLedger(eventLedger);
            }

            sendMessageToLocal(eventLedger);

        }

        return true;
    }

    @Override
    public boolean sendUnicastEvent(@NotNull UnicastEventAction unicastEventAction) {
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

        if (comms != null) {
            sender = new BezirkZirkEndPoint(comms.getNodeId(), zirkId);
        } else {
            sender = new BezirkZirkEndPoint(ID, zirkId);
        }

        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(sender));

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

            if (comms != null) {
                comms.sendEventLedger(eventLedger);
            }

            sendMessageToLocal(eventLedger);
        }
        return true;
    }


    /**
     * send the event messages to local zirks
     */
    private void sendMessageToLocal(EventLedger eventLedger) {
        processEvent(eventLedger);
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

    /**
     * called on incoming message and loop back message
     */
    @Override
    public boolean processEvent(final EventLedger eLedger) {
        final Set<ZirkId> zirkList = getAssociatedZirkList(eLedger);

        if (null == zirkList || zirkList.isEmpty()) {
            return false;
        }

        if (!decryptMsg(eLedger)) {
            return false;
        }

        if (remoteLog != null) {
            remoteLog.sendRemoteLogToServer(eLedger);
        }

        // give callback to appropriate zirk
        triggerMessageHandler(eLedger, zirkList);

        return true;
    }

    private void triggerMessageHandler(@NotNull final EventLedger eLedger,
                                       Set<ZirkId> invokeList) {
        // check if the zirk exists in that sphere then give callback
        for (ZirkId zirkId : invokeList) {
            if (!isServiceInSphere(zirkId, eLedger.getHeader().getSphereId())) {
                logger.debug("Unknown Zirk ID when attempting to trigger message handler");
                continue;
            }

            final UnicastEventAction eventMessage = new UnicastEventAction(BezirkAction.ACTION_ZIRK_RECEIVE_EVENT,
                    zirkId, eLedger.getHeader().getSender(), eLedger.getSerializedMessage(),
                    eLedger.getHeader().getUniqueMsgId(), eLedger.getHeader().getEventName(),
                    eLedger.getHeader().isIdentified());

            if (eLedger.getHeader().isIdentified()) {
                eventMessage.setAlias(eLedger.getHeader().getAlias());

                if (identityManager.isMiddlewareUser(eLedger.getHeader().getAlias())) {
                    eventMessage.setMiddlewareUser(true);
                }
            }

            msgHandler.onIncomingEvent(eventMessage);
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
            Location targetLocation = mHeader.getRecipientSelector() == null ? null :
                    mHeader.getRecipientSelector().getLocation();
            zirkList = this.checkMulticastEvent(mHeader.getEventName(), targetLocation);
        } else {
            UnicastHeader uHeader = (UnicastHeader) eLedger.getHeader();
            if (this.checkUnicastEvent(uHeader.getEventName(), uHeader.getRecipient().getBezirkZirkId())) {
                zirkList = new HashSet<>();
                zirkList.add(uHeader.getRecipient().getBezirkZirkId());
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
                logger.debug("Decryption Failed: sphereId-{} may not exist",
                        eLedger.getHeader().getSphereId());
                return false;
            }

        } else {
            //if it is local message
            if (eLedger.getEncryptedMessage() == null) {
                decryptedEventMsg = eLedger.getSerializedMessage();
            } else {
                try {
                    decryptedEventMsg = new String(eLedger.getEncryptedMessage(), WireMessage.ENCODING);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getLocalizedMessage());
                    throw new AssertionError(e);
                }
            }
        }


        eLedger.setSerializedMessage(decryptedEventMsg);
        return true;
    }


    private boolean checkUnicastEvent(String eventName, ZirkId recipient) {
        if (!ValidatorUtility.checkBezirkZirkId(recipient)) {
            logger.error("Unicast Event Check failed -> Recipient is not valid");
            return false;
        }
        return pubSubBrokerRegistry.checkUnicastEvent(eventName, recipient);
    }

    // Return a HashSet<ZirkId> by creating a new one otherwise the receiving components can modify it!
    private Set<ZirkId> checkMulticastEvent(String eventName, Location location) {
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

    private void persistRegistry() {
        try {
            pubSubBrokerStorage.persistPubSubBrokerRegistry();
        } catch (Exception e) {
            logger.error("Error in storing data \n", e);
        }
    }


}
