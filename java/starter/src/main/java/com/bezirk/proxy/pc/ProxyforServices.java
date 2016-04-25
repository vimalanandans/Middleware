/**
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @description: This class implements Android-specific Uhu Proxy.
 */
package com.bezirk.proxy.pc;


import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.IUhuComms;
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
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sadl.ISadlRegistry;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.rtc.ISignaling;
import com.bezirk.streaming.rtc.SignalingFactory;
import com.bezirk.util.UhuValidatorUtility;
import com.bezrik.network.UhuNetworkUtilities;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

public class ProxyforServices implements UhuProxyForServiceAPI {
    private static final Logger log = LoggerFactory.getLogger(ProxyforServices.class);
    private ISadlRegistry sadlRegistry = null;
    private IUhuComms comms = null;

    @Override
    public void registerService(final UhuServiceId serviceId, final String serviceName) {
        // Step 1: Register with SADL
        boolean isSADLPassed = sadlRegistry.registerService(serviceId);
        if (isSADLPassed) {
            // Step 2: moved to outside since the sphere persistence is not ready
        } else {
            log.debug("Don't need to register Service: Service ID is already registered");
        }
        boolean isSpherePassed = UhuCompManager.getSphereRegistration().registerService(serviceId, serviceName);
        if (isSpherePassed) {
            log.info("Service Registration Complete for: " + serviceName + ", " + serviceId);
            return;
        } else {
            // unregister the sadl due to failure in sphere
            log.error("Sphere Registration Failed. unregistring SADL");
            sadlRegistry.unregisterService(serviceId);

        }
    }

    @Override
    public void subscribeService(final UhuServiceId serviceId, final SubscribedRole pRole) {
        sadlRegistry.subscribeService(serviceId, pRole);
    }

    @Override
    public void sendMulticastEvent(final UhuServiceId serviceId, final Address address, final String serializedEventMsg) {

        final Iterable<String> listOfSphere = UhuCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            log.error("Service Not Registered with any Sphere");
            return;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final UhuServiceEndPoint senderSEP = UhuNetworkUtilities.getServiceEndPoint(serviceId);
        final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(senderSEP));
        final StringBuilder eventTopic = new StringBuilder(Event.fromJson(serializedEventMsg, Event.class).topic);

        while (sphereIterator.hasNext()) {
            final EventLedger ecMessage = new EventLedger();
            ecMessage.setIsMulticast(true);
            ecMessage.setSerializedMessage(serializedEventMsg);
            ecMessage.setIsLocal(true);
            final MulticastHeader mHeader = new MulticastHeader();
            mHeader.setAddress(address);
            mHeader.setSenderSEP(senderSEP);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setTopic(eventTopic.toString());
            mHeader.setSphereName(sphereIterator.next());
            ecMessage.setHeader(mHeader);
            ecMessage.setSerializedHeader(mHeader.serialize());
            //MessageQueueManager.getSendingMessageQueue().addToQueue(ecMessage);
            if (UhuValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                log.error("Comms manager not initialized");
            }

        }

    }

    @Override
    public void sendUnicastEvent(final UhuServiceId serviceId, final UhuServiceEndPoint recipient, final String serializedEventMsg) {
        final Iterable<String> listOfSphere = UhuCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            log.error("Service Not Registered with the Sphere");
            return;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final UhuServiceEndPoint senderSEP = UhuNetworkUtilities.getServiceEndPoint(serviceId);
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
            if (UhuValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                log.error("Comms manager not initialized");
            }
        }
    }

    @Override
    public void discover(final UhuServiceId serviceId, final Address address, final SubscribedRole pRole, final int discoveryId, final long timeout, final int maxDiscovered) {
        final Iterable<String> listOfSphere = UhuCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            log.error("Service Not Registered with the Sphere");
            return;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final UhuServiceEndPoint senderSEP = UhuNetworkUtilities.getServiceEndPoint(serviceId);
        final Location loc = UhuValidatorUtility.isObjectNotNull(address) ? address.getLocation() : null;

        while (sphereIterator.hasNext()) {
            final ControlLedger ControlLedger = new ControlLedger();
            final String tempSphereName = sphereIterator.next();
            final DiscoveryRequest discoveryRequest = new DiscoveryRequest(tempSphereName, senderSEP, loc, pRole, discoveryId, timeout, maxDiscovered);
            ControlLedger.setSphereId(tempSphereName);
            ControlLedger.setMessage(discoveryRequest);
            ControlLedger.setSerializedMessage(ControlLedger.getMessage().serialize());
            //MessageQueueManager.getControlSenderQueue().addToQueue(ControlLedger);
            if (UhuValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ControlLedger);
            } else {
                log.error("Comms manager not initialized");
            }
        }
        final DiscoveryLabel discoveryLabel = new DiscoveryLabel(senderSEP, discoveryId);
        final DiscoveryRecord pendingRequest = new DiscoveryRecord(timeout, maxDiscovered);
        DiscoveryProcessor.getDiscovery().addRequest(discoveryLabel, pendingRequest);
    }

    @Override
    public short sendStream(UhuServiceId senderId, UhuServiceEndPoint receiver, String serializedStream, String filePath, short streamId) {
        final Iterable<String> listOfSphere = UhuCompManager.getSphereForSadl().getSphereMembership(senderId);
        if (null == listOfSphere) {
            log.error("Service Not Registered with any Sphere: " + senderId);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            final UhuServiceEndPoint senderSEP = UhuNetworkUtilities.getServiceEndPoint(senderId);
            final String streamRequestKey = senderSEP.device + ":" + senderSEP.getUhuServiceId().getUhuServiceId() + ":" + streamId;
            final Stream stream = new Gson().fromJson(serializedStream, Stream.class);

            final StreamRecord streamRecord = new StreamRecord();
            streamRecord.localStreamId = streamId;
            streamRecord.senderSEP = senderSEP;
            streamRecord.allowDrops = false;
            streamRecord.isIncremental = false;
            streamRecord.isEncrypted = stream.isEncrypted();
            streamRecord.Sphere = null;
            streamRecord.streamStatus = StreamRecord.StreamingStatus.PENDING;
            streamRecord.recipientIP = receiver.device;
            streamRecord.recipientPort = 0;
            streamRecord.filePath = filePath;
            streamRecord.pipedInputStream = null;
            streamRecord.recipientSEP = receiver;
            streamRecord.serializedStream = serializedStream;
            streamRecord.streamTopic = stream.topic;

            boolean streamStoreStatus = comms.registerStreamBook(streamRequestKey, streamRecord);
            if (!streamStoreStatus) {
                log.error("Cannot Register Stream, CtrlMsgId is already present in StreamBook");
                return (short) -1;
            }
            final File tempFile = new File(filePath);
            while (sphereIterator.hasNext()) {
                final String sphereName = sphereIterator.next();
                final ControlLedger tcMessage = new ControlLedger();
                tcMessage.setSphereId(sphereName);
                final StreamRequest request = new StreamRequest(senderSEP, receiver, sphereName, streamRequestKey, null, serializedStream, stream.topic, tempFile.getName(), streamRecord.isEncrypted, streamRecord.isIncremental, streamRecord.allowDrops, streamId);
                tcMessage.setSphereId(sphereName);
                tcMessage.setMessage(request);
                tcMessage.setSerializedMessage(new Gson().toJson(request));
                //MessageQueueManager.getControlSenderQueue().addToQueue(tcMessage);
                if (UhuValidatorUtility.isObjectNotNull(comms)) {
                    comms.sendMessage(tcMessage);
                } else {
                    log.error("Comms manager not initialized");
                }
            }
        } catch (Exception e) {
            log.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }

    @Override
    public void setLocation(final UhuServiceId serviceId, final Location location) {
        sadlRegistry.setLocation(serviceId, location);
    }

    @Override
    public void unsubscribe(final UhuServiceId serviceId, final SubscribedRole role) {
        sadlRegistry.unsubscribe(serviceId, role);
    }

    @Override
    public void unregister(UhuServiceId serviceId) {
        sadlRegistry.unregisterService(serviceId);
    }


    public ISadlRegistry getSadlRegistry() {
        return sadlRegistry;
    }


    public void setSadlRegistry(ISadlRegistry sadlRegistry) {
        this.sadlRegistry = sadlRegistry;
    }


    public void setCommsManager(IUhuComms comms) {
        this.comms = comms;
    }


    @Override
    public short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver,
                            String serialsedString, short streamId) {

        ISignaling signalling = null;
        if (SignalingFactory.getSignalingInstance() instanceof ISignaling) {
            signalling = (ISignaling) SignalingFactory.getSignalingInstance();
        }
        if (signalling == null) {
            log.error("Feature not enabled.");
            return (short) -1;
        }

        Iterable<String> listOfSphere = UhuCompManager.getSphereForSadl().getSphereMembership(sender);
        if (null == listOfSphere) {
            log.error("Service Not Registered with any Sphere: " + sender);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            UhuServiceEndPoint senderSEP = UhuNetworkUtilities.getServiceEndPoint(sender);
            String streamRequestKey = senderSEP.device + ":" + senderSEP.getUhuServiceId().getUhuServiceId() + ":" + streamId;

            String sphereId = null;
            while (sphereIterator.hasNext()) {
                sphereId = sphereIterator.next();
                if (UhuCompManager.getSphereForSadl().isServiceInSphere(receiver.getUhuServiceId(), sphereId)) {
                    log.debug("Found the sphere:" + sphereId);
                    break;
                }
            }
            RTCControlMessage request = new RTCControlMessage(senderSEP, receiver, sphereId, streamRequestKey, RTCControlMessage.RTCControlMessageType.RTCSessionId, null);
            if (signalling != null) {
                signalling.startSignaling(request);
            }
        } catch (Exception e) {
            log.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }
}