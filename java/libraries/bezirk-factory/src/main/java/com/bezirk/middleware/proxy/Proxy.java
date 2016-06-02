package com.bezirk.middleware.proxy;

import com.bezirk.callback.pc.CBkForZirkPC;
import com.bezirk.callback.pc.BroadcastReceiver;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.persistence.BezirkProxyPersistence;
import com.bezirk.persistence.BezirkProxyRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.pc.ProxyForServices;
import com.bezirk.proxy.registration.ServiceRegistration;
import com.bezirk.starter.MainService;
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
    private final ProxyForServices proxy;
    private final ProxyUtil proxyUtil;
    private final BezirkProxyPersistence proxyPersistence;
    private final MainService mainService;
    // Stream
    private short streamFactory = 0;
    private BezirkProxyRegistry bezirkProxyRegistry = null;

    public Proxy() {
        proxy = new ProxyForServices();
        proxyUtil = new ProxyUtil();
        mainService = new MainService(proxy, null);
        final BroadcastReceiver brForService = new BRForService(activeStreams, dListenerMap,
                eventListenerMap, sidMap, streamListenerMap);
        CBkForZirkPC bezirkPcCallback = new CBkForZirkPC(brForService);
        mainService.startStack(bezirkPcCallback);
        proxyPersistence = mainService.getBezirkProxyPersistence();
        try {
            bezirkProxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
        } catch (Exception e) {
            logger.error("Error loading BezirkProxyRegistry", e);
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

        String zirkIdAsString = bezirkProxyRegistry.getBezirkServiceId(zirkName);

        if (null == zirkIdAsString) {
            zirkIdAsString = ServiceRegistration.generateUniqueServiceID() + ":" + zirkName;
            bezirkProxyRegistry.updateBezirkZirkId(zirkName, zirkIdAsString);
            try {
                proxyPersistence.persistBezirkProxyRegistry();
            } catch (Exception e) {
                logger.error("Error in persisting the information", e);
            }
        }
        logger.info("Zirk-Id-> " + zirkIdAsString);
        final BezirkZirkId serviceId = new BezirkZirkId(zirkIdAsString);
        // Register with Bezirk
        proxy.registerService(serviceId, zirkName);
        return serviceId;
    }

    @Override
    public void unregisterZirk(ZirkId zirkId) {
        if (null == zirkId) {
            logger.error("Trying to unregister with null ID");
            return;
        }
        // Clear the Persistence by removing the BezirkZirkId of the unregistering Zirk
        BezirkZirkId sId = (BezirkZirkId) zirkId;
        bezirkProxyRegistry.deleteBezirkZirkId(sId.getBezirkZirkId());
        try {
            proxyPersistence.persistBezirkProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in persisting the information", e);
        }
        proxy.unregister((BezirkZirkId) zirkId);
    }

    @Override
    public void subscribe(final ZirkId subscriber, final ProtocolRole protocolRole, final BezirkListener listener) {
        if (null == protocolRole.getRoleName() || protocolRole.getRoleName().isEmpty() || null == listener || null == subscriber) {
            logger.error("Check for ProtocolRole/ BezirkListener/ZirkId for null or empty values");
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
    public void sendEvent(ZirkId sender, RecipientSelector recipient,
                          Event event) {
        // Check for sending the target!
        if (null == event || null == sender) {
            logger.error("Check for null in target or Event or sender");
            return;
        }
        proxy.sendMulticastEvent((BezirkZirkId) sender, recipient, event.toJson());
    }

    @Override
    public void sendEvent(ZirkId sender,
                          ZirkEndPoint recipient,
                          Event event) {
        if (null == recipient || null == event || null == sender) {
            logger.error("Check for null in receiver or Event or sender");
            return;
        }
        proxy.sendUnicastEvent((BezirkZirkId) sender, (BezirkZirkEndPoint) recipient, event.toJson());
    }

    @Override
    public short sendStream(ZirkId sender,
                            ZirkEndPoint recipient,
                            Stream stream, PipedOutputStream dataStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(((BezirkZirkId) sender).getBezirkZirkId() + streamId, stream.topic);
        BezirkZirkEndPoint recipientSEP = (BezirkZirkEndPoint) recipient;
        proxy.sendStream((BezirkZirkId) sender, recipientSEP, stream.toJson(), streamId);
        return streamId;
    }

    @Override
    public short sendStream(ZirkId sender, ZirkEndPoint recipient, Stream stream, File file) {
        if (null == recipient || null == stream || null == file || file == null || stream.topic.isEmpty()) {
            logger.error("Check for null values in sendStream()/ Topic might be Empty");
            return (short) -1;
        }
        if (!file.exists()) {
            logger.error("Cannot send file stream. File not found: " + file);
            return (short) -1;
        }

        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(((BezirkZirkId) sender).getBezirkZirkId() + streamId, stream.topic);
        proxy.sendStream((BezirkZirkId) sender, (BezirkZirkEndPoint) recipient, stream.toJson(), file, streamId);
        return streamId;
    }

    @Override
    public void requestPipeAuthorization(ZirkId requester, Pipe pipe, PipePolicy allowedIn,
                                         PipePolicy allowedOut, BezirkListener listener) {
        // TODO: Design and implement pipes API
    }

    @Override
    public void getPipePolicy(Pipe pipe,
                              BezirkListener listener) {
        // TODO: Design and implement pipes API
    }

    @Override
    public void discover(ZirkId zirk, RecipientSelector scope,
                         ProtocolRole protocolRole, long timeout,
                         int maxResults, BezirkListener listener) {
        // update discovery map
        discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;
        dListenerMap.put((BezirkZirkId) zirk, new DiscoveryBookKeeper(discoveryCount, listener));
        proxy.discover((BezirkZirkId) zirk, scope, new SubscribedRole(protocolRole), discoveryCount, timeout, maxResults);
    }

    @Override
    public void setLocation(ZirkId zirk, Location location) {
        if (location == null) {
            logger.error("Location is null or Empty, Services cannot set the location as null");
        } else {
            proxy.setLocation((BezirkZirkId) zirk, location);
        }
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
