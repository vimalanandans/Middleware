/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 *
 * @description: This class implements Android-specific Bezirk Proxy.
 *
 */

package com.bezirk.proxy.android;

import android.app.Service;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.GenerateMsgId;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.streaming.rtc.RTCControlMessage;
import com.bezirk.discovery.DiscoveryLabel;
import com.bezirk.discovery.DiscoveryProcessor;
import com.bezirk.discovery.DiscoveryRecord;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.sadl.ISadlRegistry;
import com.bezirk.starter.helper.BezirkStackHandler;
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

public class ProxyForZirks implements BezirkProxyForServiceAPI {
    private static final Logger logger = LoggerFactory.getLogger(ProxyForZirks.class);

    // TODO: do we need do know who the zirk is???  It looks like this variable is not used!
    private static Service serviceInstance;
    private final ProxyForZirksHelper proxyForZirksHelper;
    private ISadlRegistry sadlRegistry;
    private BezirkComms comms;

    public ProxyForZirks(Service service) {
        serviceInstance = service;
        proxyForZirksHelper = new ProxyForZirksHelper();
    }

    public static Service getServiceInstance() {
        return serviceInstance;
    }

    @Override
    public void registerService(final BezirkZirkId serviceId, final String serviceName) {

        //TODO this code is common across all the methods here, implement a annotation.
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }


        // Step 1: Register with SADL
        boolean isSADLPassed = sadlRegistry.registerService(serviceId);

        if (isSADLPassed) {
            // Step 2: moved to outside since the sphere persistence is not ready
        } else {
            logger.error("SADL Registration failed, Zirk ID is already registered");
        }

        // Step 2: Register with sphere
        boolean isSpherePassed = BezirkCompManager.getSphereRegistration().registerService(serviceId, serviceName);

        if (isSpherePassed) {
            logger.info("Zirk Registration Complete for: " + serviceName + ", " + serviceId);
            return;
        } else {
            // unregister the sadl due to failure in sphere
            logger.error("sphere Registration Failed. unregistring SADL");
            sadlRegistry.unregisterService(serviceId);

        }
    }

    @Override
    public void subscribeService(final BezirkZirkId serviceId, final SubscribedRole pRole) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }
        sadlRegistry.subscribeService(serviceId, pRole);
    }

    @Override
    public void sendMulticastEvent(final BezirkZirkId serviceId, final Address address, final String serializedEventMsg) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + serviceId.getBezirkZirkId());
            return;
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
            mHeader.setAddress(address);
            mHeader.setSenderSEP(senderSEP);
            mHeader.setUniqueMsgId(uniqueMsgId.toString());
            mHeader.setTopic(eventTopic.toString());
            mHeader.setSphereName(sphereIterator.next());
            ecMessage.setHeader(mHeader);
            ecMessage.setSerializedHeader(mHeader.serialize());
            if (BezirkValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                logger.error("Comms manager not initialized");
            }
        }

    }

    @Override
    public void sendUnicastEvent(final BezirkZirkId serviceId, final BezirkZirkEndPoint recipient, final String serializedEventMsg) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with the sphere");
            return;
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
            if (BezirkValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(ecMessage);
            } else {
                logger.error("Comms manager not initialized");
            }
        }

    }

    @Override
    public void discover(final BezirkZirkId serviceId, final Address address, final SubscribedRole pRole, final int discoveryId, final long timeout, final int maxDiscovered) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(serviceId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with the sphere");
            return;
        }

        final Iterator<String> sphereIterator = listOfSphere.iterator();
        final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        final Location loc = (address == null) ? null : address.getLocation();

        while (sphereIterator.hasNext()) {
            final ControlLedger ControlLedger = new ControlLedger();
            final String tempSphereName = sphereIterator.next();
            final DiscoveryRequest discoveryRequest = new DiscoveryRequest(tempSphereName, senderSEP, loc, pRole, discoveryId, timeout, maxDiscovered);
            ControlLedger.setSphereId(tempSphereName);
            ControlLedger.setMessage(discoveryRequest);
            ControlLedger.setSerializedMessage(ControlLedger.getMessage().serialize());
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
    public short sendStream(BezirkZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return (short) -1;
        }

        final Iterable<String> listOfSphere = BezirkCompManager.getSphereForSadl().getSphereMembership(senderId);
        if (null == listOfSphere) {
            logger.error("Zirk Not Registered with any sphere: " + senderId);
            return (short) -1;
        }
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        try {
            final BezirkZirkEndPoint senderSEP = BezirkNetworkUtilities.getServiceEndPoint(senderId);
            final String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getBezirkZirkId() + ":" + streamId;
            final Stream stream = new Gson().fromJson(serializedString, Stream.class);

            final StreamRecord streamRecord = proxyForZirksHelper.prepareStreamRecord(receiver, serializedString, file, streamId, senderSEP, stream);

            boolean streamStoreStatus = comms.registerStreamBook(streamRequestKey, streamRecord);
            if (!streamStoreStatus) {
                logger.error("Cannot Register Stream, CtrlMsgId is already present in StreamBook");
                return (short) -1;
            }
            proxyForZirksHelper.sendStreamToSpheres(sphereIterator, streamRequestKey, streamRecord, file, comms);
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }

    @Override
    public short sendStream(BezirkZirkId sender, BezirkZirkEndPoint receiver, String serializedString, short streamId) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return (short) -1;
        }

        Signaling signaling = null;
        if (SignalingFactory.getSignalingInstance() instanceof Signaling) {
            signaling = (Signaling) SignalingFactory.getSignalingInstance();
        }
        if (signaling == null) {
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
            String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getBezirkZirkId() + ":" + streamId;

            String sphereId = proxyForZirksHelper.getSphereId(receiver, sphereIterator);
            RTCControlMessage request = new RTCControlMessage(senderSEP, receiver, sphereId, streamRequestKey, RTCControlMessage.RTCControlMessageType.RTCSessionId, null);
            signaling.startSignaling(request);
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return (short) -1;
        }
        return (short) 1;
    }

    @Override
    public void setLocation(final BezirkZirkId serviceId, final Location location) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }

        sadlRegistry.setLocation(serviceId, location);
    }

    @Override
    public void unsubscribe(final BezirkZirkId serviceId, final SubscribedRole role) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }

        sadlRegistry.unsubscribe(serviceId, role);
    }

    @Override
    public void unregister(BezirkZirkId serviceId) {
        //if Stack was not started correctly, return without any further actions.
        if (!BezirkStackHandler.isStackStarted()) {
            logger.error("Bezirk was not started properly!!!. Restart the stack.");
            return;
        }

        sadlRegistry.unregisterService(serviceId);
    }

    public void setSadlRegistry(ISadlRegistry sadlRegistry) {
        this.sadlRegistry = sadlRegistry;
    }

    public void setComms(BezirkComms comms) {
        this.comms = comms;
    }


}
