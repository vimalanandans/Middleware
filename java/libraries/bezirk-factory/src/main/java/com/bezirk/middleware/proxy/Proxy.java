package com.bezirk.middleware.proxy;

import com.bezirk.callback.pc.CBkForZirkPC;
import com.bezirk.callback.pc.IBoradcastReceiver;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.persistence.IUhuProxyPersistence;
import com.bezirk.persistence.UhuProxyRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.pc.ProxyforServices;
import com.bezirk.proxy.registration.ServiceRegistration;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuConfig;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;

public class Proxy implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);
    private static int discoveryCount = 0; // keep track of Discovery Id
    protected final HashMap<BezirkZirkId, DiscoveryBookKeeper> dListenerMap = new HashMap<BezirkZirkId, DiscoveryBookKeeper>();
    protected final HashMap<BezirkZirkId, HashSet<BezirkListener>> sidMap = new HashMap<BezirkZirkId, HashSet<BezirkListener>>();
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
        CBkForZirkPC uhuPcCallback = new CBkForZirkPC(brForService);
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
        CBkForZirkPC uhuPcCallback = new CBkForZirkPC(brForService);
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
    public ZirkId registerZirk(String zirkName) {
        logger.trace("inside RegisterService");
        if (zirkName == null) {
            logger.error("Zirk name Cannot be null during Registration");
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
        logger.info("Zirk-Id-> " + serviceIdAsString);
        final BezirkZirkId serviceId = new BezirkZirkId(serviceIdAsString);
        // Register with Uhu
        proxy.registerService(serviceId, zirkName);
        return serviceId;
    }

    @Override
    public void unregisterZirk(ZirkId zirkId) {
        if (null == zirkId) {
            logger.error("Trying to UnRegister with null ID");
            return;
        }
        // Clear the Persistence by removing the BezirkZirkId of the unregistering Zirk
        BezirkZirkId sId = (BezirkZirkId) zirkId;
        uhuProxyRegistry.deleteUhuServiceId(sId.getBezirkZirkId());
        try {
            proxyPersistence.persistUhuProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in persisting the information", e);
        }
        proxy.unregister((BezirkZirkId) zirkId);
    }

    @Override
    public void subscribe(final ZirkId subscriber, final ProtocolRole protocolRole, final BezirkListener listener) {
        if (null == protocolRole.getProtocolName() || protocolRole.getProtocolName().isEmpty() || null == listener || null == subscriber) {
            logger.error("Check for ProtocolRole/ UhuListener/ZirkId for null or empty values");
            return;
        }
        if (null == protocolRole.getEventTopics() && null == protocolRole.getStreamTopics()) {
            logger.error("ProtocolRole doesn't have any Events/ Streams to subscribe");
            return;
        }
        if (BezirkValidatorUtility.isObjectNotNull(protocolRole.getEventTopics())) {
            proxyUtil.addTopicsToMaps(subscriber, protocolRole.getEventTopics(),
                    listener, sidMap, eventListenerMap, "Event");
        } else {
            logger.info("No Events to Subscribe");
        }
        if (BezirkValidatorUtility.isObjectNotNull(protocolRole.getStreamTopics())) {

            proxyUtil.addTopicsToMaps(subscriber, protocolRole.getStreamTopics(),
                    listener, sidMap, streamListenerMap, "Stream");

        } else {
            logger.info("No Streams to Subscribe");
        }
        // Send the intent
        SubscribedRole subRole = new SubscribedRole(protocolRole);

        //Subscribe to protocol
        proxy.subscribeService((BezirkZirkId) subscriber, subRole);
    }

    @Override
    public boolean unsubscribe(ZirkId subscriber, ProtocolRole protocolRole) {
        if (null == subscriber || null == protocolRole) {
            logger.error("Null Values for unsubscribe method");
            return false;
        }

        return proxy.unsubscribe((BezirkZirkId) subscriber, new SubscribedRole(protocolRole));
    }

    @Override
    public void sendEvent(ZirkId sender, Address receiver,
                          Event event) {
        // Check for sending the target!
        if (null == event || null == sender) {
            logger.error("Check for null in target or Event or sender");
            return;
        }
        proxy.sendMulticastEvent((BezirkZirkId) sender, receiver, event.toJson());
    }

    @Override
    public void sendEvent(ZirkId sender,
                          ZirkEndPoint receiver,
                          Event event) {
        if (null == receiver || null == event || null == sender) {
            logger.error("Check for null in receiver or Event or sender");
            return;
        }
        proxy.sendUnicastEvent((BezirkZirkId) sender, (BezirkZirkEndPoint) receiver, event.toJson());
    }

    @Override
    public short sendStream(ZirkId sender,
                            ZirkEndPoint receiver,
                            Stream stream, PipedOutputStream dataStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(((BezirkZirkId) sender).getBezirkZirkId() + streamId, stream.topic);
        BezirkZirkEndPoint recipientSEP = (BezirkZirkEndPoint) receiver;
        proxy.sendStream((BezirkZirkId) sender, recipientSEP, stream.toJson(), streamId);
        return streamId;
    }

    @Override
    public short sendStream(ZirkId sender, ZirkEndPoint receiver, Stream stream, File file) {
        if (null == receiver || null == stream || null == file || file == null || stream.topic.isEmpty()) {
            logger.error("Check for null values in sendStream()/ Topic might be Empty");
            return (short) -1;
        }
        if (!file.exists()) {
            logger.error("Cannot send file stream. File not found: " + file);
            return (short) -1;
        }

        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(((BezirkZirkId) sender).getBezirkZirkId() + streamId, stream.topic);
        proxy.sendStream((BezirkZirkId) sender, (BezirkZirkEndPoint) receiver, stream.toJson(), file, streamId);
        return streamId;
    }

    @Override
    public void requestPipeAuthorization(ZirkId requester, Pipe pipe, PipePolicy allowedIn,
                                         PipePolicy allowedOut, BezirkListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPipePolicy(Pipe pipe,
                              BezirkListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void discover(ZirkId zirk, Address scope,
                         ProtocolRole protocolRole, long timeout,
                         int maxResults, BezirkListener listener) {
        // update discovery map
        discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;
        dListenerMap.put((BezirkZirkId) zirk, new DiscoveryBookKeeper(discoveryCount, listener));
        proxy.discover((BezirkZirkId) zirk, scope, new SubscribedRole(protocolRole), discoveryCount, timeout, maxResults);

    }

    @Override
    public void setLocation(ZirkId zirk, Location location) {
        if (null == location) {
            logger.error("Location is null or Empty, Services cannot set the location as Null");
            return;
        }
        //Set
        proxy.setLocation((BezirkZirkId) zirk, location);
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