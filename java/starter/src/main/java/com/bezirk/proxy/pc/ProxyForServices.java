/**
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @description: This class implements Android-specific Bezirk Proxy.
 */
package com.bezirk.proxy.pc;


import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.GenerateMsgId;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.control.messages.streaming.rtc.RTCControlMessage;
import com.bezirk.discovery.DiscoveryLabel;
import com.bezirk.discovery.DiscoveryProcessor;
import com.bezirk.discovery.DiscoveryRecord;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.sadl.ISadlRegistry;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.rtc.Signaling;
import com.bezirk.streaming.rtc.SignalingFactory;
import com.bezirk.util.BezirkValidatorUtility;
import com.bezrik.network.BezirkNetworkUtilities;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

public class ProxyForServices implements BezirkProxyForServiceAPI {
    private static final Logger logger = LoggerFactory.getLogger(ProxyForServices.class);
    private ISadlRegistry sadlRegistry = null;
    private BezirkComms comms = null;

    @Override
    public void registerService(final ZirkId serviceId, final String serviceName) {
        // Step 1: Register with SADL
        boolean isSADLPassed = sadlRegistry.registerService(serviceId);
        if (isSADLPassed) {
            // Step 2: moved to outside since the sphere persistence is not ready
        } else {
            logger.debug("Don't need to register Zirk: Zirk ID is already registered");
        }
        boolean isSpherePassed = BezirkCompManager.getSphereRegistration().registerZirk(serviceId, serviceName);
        if (isSpherePassed) {
            logger.info("Zirk Registration Complete for: {}, {}", serviceName, serviceId);
        } else {
            // unregister the sadl due to failure in sphere
            logger.error("Sphere Registration Failed: unregistering SADL");
            sadlRegistry.unregisterService(serviceId);
        }
    }

    @Override
    public void subscribeService(final ZirkId serviceId, final SubscribedRole pRole) {
        sadlRegistry.subscribeService(serviceId, pRole);
    }

    @Override
    public void sendMulticastEvent(final ZirkId serviceId, final RecipientSelector recipientSelector, final String serializedEventMsg) {

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere");
            return;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        final StringBuilder eventTopic = new StringBuilder(Event.fromJson(serializedEventMsg, Event.class).topic);

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
            //MessageQueueManager.getSendingMessageQueue().addToQueue(ecMessage);
            if (BezirkValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                logger.error("Comms manager not initialized");
            }

        }

    }

    @Override
    public void sendUnicastEvent(final ZirkId serviceId, final BezirkZirkEndPoint recipient, final String serializedEventMsg) {
        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk not registered with the sphere");
            return;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        final StringBuilder eventTopic = new StringBuilder(Event.fromJson(serializedEventMsg, Event.class).topic);

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
            //MessageQueueManager.getSendingMessageQueue().addToQueue(ecMessage);
            if (BezirkValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                logger.error("Comms manager not initialized");
            }
        }
    }

    @Override
    public void discover(final ZirkId serviceId, final RecipientSelector recipientSelector, final SubscribedRole pRole, final int discoveryId, final long timeout, final int maxDiscovered) {
        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk not tegistered with the sphere");
            return;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final Location loc = BezirkValidatorUtility.isObjectNotNull(recipientSelector) ? recipientSelector.getLocation() : null;

        while (sphereIterator.hasNext()) {
            final ControlLedger ControlLedger = new ControlLedger();
            final String tempSphereName = sphereIterator.next();
            final DiscoveryRequest discoveryRequest = new DiscoveryRequest(tempSphereName, senderSEP, loc, pRole, discoveryId, timeout, maxDiscovered);
            ControlLedger.setSphereId(tempSphereName);
            ControlLedger.setMessage(discoveryRequest);
            ControlLedger.setSerializedMessage(ControlLedger.getMessage().serialize());
            //MessageQueueManager.getControlSenderQueue().addToQueue(ControlLedger);
            if (BezirkValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ControlLedger);
            } else {
                logger.error("Comms manager not initialized");
            }
        }
        final DiscoveryLabel discoveryLabel = new DiscoveryLabel(senderSEP, discoveryId);
        final DiscoveryRecord pendingRequest = new DiscoveryRecord(timeout, maxDiscovered);
        DiscoveryProcessor.getDiscovery().addRequest(discoveryLabel, pendingRequest);
    }

    @Override
    public short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId) {
        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(senderId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + senderId);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(senderId);
            final String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getZirkId() + ":" + streamId;
            final Stream stream = new Gson().fromJson(serializedString, Stream.class);

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
            streamRecord.serializedStream = serializedString;
            streamRecord.streamTopic = stream.topic;

            boolean streamStoreStatus = comms.registerStreamBook(streamRequestKey, streamRecord);
            if (!streamStoreStatus) {
                logger.error("Cannot Register Stream, CtrlMsgId is already present in StreamBook");
                return (short) -1;
            }

            while (sphereIterator.hasNext()) {
                final String sphereName = sphereIterator.next();
                final ControlLedger tcMessage = new ControlLedger();
                tcMessage.setSphereId(sphereName);
                final StreamRequest request = new StreamRequest(senderSEP, receiver, sphereName, streamRequestKey, null, serializedString, stream.topic, file.getName(), streamRecord.isEncrypted, streamRecord.isIncremental, streamRecord.isReliable, streamId);
                tcMessage.setSphereId(sphereName);
                tcMessage.setMessage(request);
                tcMessage.setSerializedMessage(new Gson().toJson(request));
                //MessageQueueManager.getControlSenderQueue().addToQueue(tcMessage);
                if (BezirkValidatorUtility.isObjectNotNull(comms)) {
                    comms.sendMessage(tcMessage);
                } else {
                    logger.error("Comms manager not initialized");
                }
            }
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }

    @Override
    public void setLocation(final ZirkId serviceId, final Location location) {
        sadlRegistry.setLocation(serviceId, location);
    }

    @Override
    public boolean unsubscribe(final ZirkId serviceId, final SubscribedRole role) {
        return sadlRegistry.unsubscribe(serviceId, role);
    }

    @Override
    public void unregister(ZirkId serviceId) {
        sadlRegistry.unregisterService(serviceId);
    }


    public ISadlRegistry getSadlRegistry() {
        return sadlRegistry;
    }


    public void setSadlRegistry(ISadlRegistry sadlRegistry) {
        this.sadlRegistry = sadlRegistry;
    }


    public void setCommsManager(BezirkComms comms) {
        this.comms = comms;
    }


    @Override
    public short sendStream(ZirkId sender, BezirkZirkEndPoint receiver,
                            String serializedString, short streamId) {

        Signaling signalling = null;
        if (SignalingFactory.getSignalingInstance() instanceof Signaling) {
            signalling = (Signaling) SignalingFactory.getSignalingInstance();
        }
        if (signalling == null) {
            logger.error("Feature not enabled.");
            return (short) -1;
        }

        Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(sender);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + sender);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(sender);
            String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getZirkId() + ":" + streamId;

            String sphereId = null;
            while (sphereIterator.hasNext()) {
                sphereId = sphereIterator.next();
                if (BezirkCompManager.getSphereForSadl().isZirkInSphere(receiver.getBezirkZirkId(), sphereId)) {
                    logger.debug("Found the sphere:" + sphereId);
                    break;
                }
            }
            RTCControlMessage request = new RTCControlMessage(senderSEP, receiver, sphereId, streamRequestKey, RTCControlMessage.RTCControlMessageType.RTCSessionId, null);
            if (signalling != null) {
                signalling.startSignaling(request);
            }
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }
}
