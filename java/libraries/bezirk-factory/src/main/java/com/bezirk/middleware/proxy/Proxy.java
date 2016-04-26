package com.bezirk.middleware.proxy;

import com.bezirk.callback.pc.CBkForServicePC;
import com.bezirk.callback.pc.IBoradcastReceiver;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.persistence.IUhuProxyPersistence;
import com.bezirk.persistence.UhuProxyRegistry;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.proxy.pc.ProxyforServices;
import com.bezirk.proxy.registration.ServiceRegistration;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuConfig;
import com.bezirk.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;

public class Proxy implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);
    private static int discoveryCount = 0; // keep track of Discovery Id
    protected final HashMap<UhuServiceId, DiscoveryBookKeeper> dListenerMap = new HashMap<UhuServiceId, DiscoveryBookKeeper>();
    protected final HashMap<UhuServiceId, HashSet<BezirkListener>> sidMap = new HashMap<UhuServiceId, HashSet<BezirkListener>>();
    protected final HashMap<String, HashSet<BezirkListener>> eventListenerMap = new HashMap<String, HashSet<BezirkListener>>();
    protected final HashMap<String, HashSet<BezirkListener>> streamListenerMap = new HashMap<String, HashSet<BezirkListener>>();
    protected final HashMap<String, String> activeStreams = new HashMap<String, String>();
    private final ProxyforServices proxy;
    private final ProxyUtil proxyUtil;
    private final IUhuProxyPersistence proxyPersistence;
    private final MainService mainService;
    // Stream
    private short streamFactory = 0;
    private UhuProxyRegistry uhuProxyRegistry = null;

    public Proxy() {
        proxy = new ProxyforServices();
        proxyUtil = new ProxyUtil();
        mainService = new MainService(proxy, null);
        final IBoradcastReceiver brForService = new BRForService(activeStreams, dListenerMap,
                eventListenerMap, sidMap, streamListenerMap);
        CBkForServicePC uhuPcCallback = new CBkForServicePC(brForService);
        mainService.startStack(uhuPcCallback);
        proxyPersistence = mainService.getUhuProxyPersistence();
        try {
            uhuProxyRegistry = proxyPersistence.loadUhuProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in Loding UhuProxyRegistry", e);
            System.exit(0);
        }
    }

    public Proxy(UhuConfig uhuConfig) {
        proxy = new ProxyforServices();
        proxyUtil = new ProxyUtil();
        mainService = new MainService(proxy, uhuConfig);
        BRForService brForService = new BRForService(activeStreams, dListenerMap,
                eventListenerMap, sidMap, streamListenerMap);
        CBkForServicePC uhuPcCallback = new CBkForServicePC(brForService);
        mainService.startStack(uhuPcCallback);
        proxyPersistence = mainService.getUhuProxyPersistence();
        try {
            uhuProxyRegistry = proxyPersistence.loadUhuProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in Loding UhuProxyRegistry", e);
            System.exit(0);
        }
    }

    @Override
    public ServiceId registerService(String zirkName) {
        logger.trace("inside RegisterService");
        if (zirkName == null) {
            logger.error("Service name Cannot be null during Registration");
            return null;
        }

        String serviceIdAsString = uhuProxyRegistry.getUhuServiceId(zirkName);

        if (null == serviceIdAsString) {
            serviceIdAsString = ServiceRegistration.generateUniqueServiceID() + ":" + zirkName;
            uhuProxyRegistry.updateUhuServiceId(zirkName, serviceIdAsString);
            try {
                proxyPersistence.persistUhuProxyRegistry();
            } catch (Exception e) {
                logger.error("Error in persisting the information", e);
            }
        }
        logger.info("Service-Id-> " + serviceIdAsString);
        final UhuServiceId serviceId = new UhuServiceId(serviceIdAsString);
        // Register with Uhu
        proxy.registerService(serviceId, zirkName);
        return serviceId;
    }

    @Override
    public void unregisterService(ServiceId zirkId) {
        if (null == zirkId) {
            logger.error("Trying to UnRegister with null ID");
            return;
        }
        // Clear the Persistence by removing the UhuServiceId of the unregistering Service
        UhuServiceId sId = (UhuServiceId) zirkId;
        uhuProxyRegistry.deleteUhuServiceId(sId.getUhuServiceId());
        try {
            proxyPersistence.persistUhuProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in persisting the information", e);
        }
        proxy.unregister((UhuServiceId) zirkId);
    }

    @Override
    public void subscribe(final ServiceId subscriber, final ProtocolRole protocolRole, final BezirkListener listener) {
        if (null == protocolRole.getProtocolName() || protocolRole.getProtocolName().isEmpty() || null == listener || null == subscriber) {
            logger.error("Check for ProtocolRole/ UhuListener/ServiceId for null or empty values");
            return;
        }
        if (null == protocolRole.getEventTopics() && null == protocolRole.getStreamTopics()) {
            logger.error("ProtocolRole doesn't have any Events/ Streams to subscribe");
            return;
        }
        if (UhuValidatorUtility.isObjectNotNull(protocolRole.getEventTopics())) {
            proxyUtil.addTopicsToMaps(subscriber, protocolRole.getEventTopics(),
                    listener, sidMap, eventListenerMap, "Event");
        } else {
            logger.info("No Events to Subscribe");
        }
        if (UhuValidatorUtility.isObjectNotNull(protocolRole.getStreamTopics())) {

            proxyUtil.addTopicsToMaps(subscriber, protocolRole.getStreamTopics(),
                    listener, sidMap, streamListenerMap, "Stream");

        } else {
            logger.info("No Streams to Subscribe");
        }
        // Send the intent
        SubscribedRole subRole = new SubscribedRole(protocolRole);

        //Subscribe to protocol
        proxy.subscribeService((UhuServiceId) subscriber, subRole);
    }

    @Override
    public void unsubscribe(ServiceId subscriber,
                            ProtocolRole protocolRole) {
        if (null == subscriber || null == protocolRole) {
            logger.error("Null Values for unsubscribe method");
            return;
        }
        proxy.unsubscribe((UhuServiceId) subscriber, new SubscribedRole(protocolRole));

    }

    @Override
    public void sendEvent(ServiceId sender, Address receiver,
                          Event event) {
        // Check for sending the target!
        if (null == event || null == sender) {
            logger.error("Check for null in target or Event or sender");
            return;
        }
        proxy.sendMulticastEvent((UhuServiceId) sender, receiver, event.toJson());
    }

    @Override
    public void sendEvent(ServiceId sender,
                          ServiceEndPoint receiver,
                          Event event) {
        if (null == receiver || null == event || null == sender) {
            logger.error("Check for null in receiver or Event or sender");
            return;
        }
        proxy.sendUnicastEvent((UhuServiceId) sender, (UhuServiceEndPoint) receiver, event.toJson());
    }

    @Override
    public short sendStream(ServiceId sender,
                            ServiceEndPoint receiver,
                            Stream stream, PipedOutputStream dataStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(((UhuServiceId) sender).getUhuServiceId() + streamId, stream.topic);
        UhuServiceEndPoint recipientSEP = (UhuServiceEndPoint) receiver;
        proxy.sendStream((UhuServiceId) sender, recipientSEP, stream.toJson(), streamId);
        return streamId;
    }

    @Override
    public short sendStream(ServiceId sender, ServiceEndPoint receiver, Stream stream, File file) {
        if (null == receiver || null == stream || null == file || file == null || stream.topic.isEmpty()) {
            logger.error("Check for null values in sendStream()/ Topic might be Empty");
            return (short) -1;
        }
        if (!file.exists()) {
            logger.error("Cannot send file stream. File not found: " + file);
            return (short) -1;
        }

        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(((UhuServiceId) sender).getUhuServiceId() + streamId, stream.topic);
        proxy.sendStream((UhuServiceId) sender, (UhuServiceEndPoint) receiver, stream.toJson(), file, streamId);
        return streamId;
    }

    @Override
    public void requestPipeAuthorization(ServiceId requester, Pipe pipe, PipePolicy allowedIn,
                                         PipePolicy allowedOut, BezirkListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPipePolicy(Pipe pipe,
                              BezirkListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void discover(ServiceId zirk, Address scope,
                         ProtocolRole protocolRole, long timeout,
                         int maxResults, BezirkListener listener) {
        // update discovery map
        discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;
        dListenerMap.put((UhuServiceId) zirk, new DiscoveryBookKeeper(discoveryCount, listener));
        proxy.discover((UhuServiceId) zirk, scope, new SubscribedRole(protocolRole), discoveryCount, timeout, maxResults);

    }

    @Override
    public void setLocation(ServiceId zirk, Location location) {
        if (null == location) {
            logger.error("Location is null or Empty, Services cannot set the location as Null");
            return;
        }
        //Set
        proxy.setLocation((UhuServiceId) zirk, location);
    }

    //Create object for listener, discoveryId pair
    class DiscoveryBookKeeper {
        private final int discoveryId;
        private final BezirkListener listener;

        DiscoveryBookKeeper(int id, BezirkListener listener) {
            this.discoveryId = id;
            this.listener = listener;
        }

        public int getDiscoveryId() {
            return discoveryId;
        }

        public BezirkListener getListener() {
            return listener;
        }

    }

}
