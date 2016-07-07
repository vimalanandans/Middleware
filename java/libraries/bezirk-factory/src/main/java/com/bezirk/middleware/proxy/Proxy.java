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
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.persistence.BezirkProxyPersistence;
import com.bezirk.persistence.BezirkProxyRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.pc.ProxyForServices;
import com.bezirk.proxy.ServiceRegistration;
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
    protected final HashMap<ZirkId, DiscoveryBookKeeper> dListenerMap = new HashMap<ZirkId, DiscoveryBookKeeper>();
    protected final HashMap<ZirkId, HashSet<BezirkListener>> sidMap = new HashMap<ZirkId, HashSet<BezirkListener>>();
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

    private ZirkId zirkId;

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
            System.exit(-1);
        }
    }

    public boolean registerZirk(String zirkName) {
        logger.trace("inside RegisterService");
        if (zirkName == null) {
            throw new IllegalArgumentException("Cannot register a Zirk with a null name");
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

        if (logger.isDebugEnabled()) logger.debug("Zirk-Id: {}", zirkIdAsString);

        zirkId = new ZirkId(zirkIdAsString);
        // Register with Bezirk
        proxy.registerService(zirkId, zirkName);
        return true;
    }

    @Override
    public void unregisterZirk() {
        // Clear the Persistence by removing the ZirkId of the unregistering Zirk
        ZirkId sId = (ZirkId) zirkId;
        bezirkProxyRegistry.deleteBezirkZirkId(sId.getZirkId());
        try {
            proxyPersistence.persistBezirkProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in persisting the information", e);
        }
        proxy.unregister((ZirkId) zirkId);
    }

    @Override
    public void subscribe(final ProtocolRole protocolRole, final BezirkListener listener) {
        if (protocolRole.getRoleName() == null || protocolRole.getRoleName().isEmpty()) {
            throw new IllegalArgumentException("Cannot subscribe to an empty role");
        }

        if (protocolRole.getEventTopics() == null && protocolRole.getStreamTopics() == null) {
            throw new IllegalArgumentException("Passed protocolRole does not specify any events or " +
                    "streams to subscribe to");
        }

        if (listener == null) {
            throw new IllegalArgumentException("No listener specified when subscribing to role");
        }

        if (BezirkValidatorUtility.isObjectNotNull(protocolRole.getEventTopics())) {
            proxyUtil.addTopicsToMaps(zirkId, protocolRole.getEventTopics(),
                    listener, sidMap, eventListenerMap, "Event");
        } else {
            logger.info("No Events to Subscribe");
        }
        if (BezirkValidatorUtility.isObjectNotNull(protocolRole.getStreamTopics())) {

            proxyUtil.addTopicsToMaps(zirkId, protocolRole.getStreamTopics(),
                    listener, sidMap, streamListenerMap, "Stream");

        } else {
            logger.info("No Streams to Subscribe");
        }
        // Send the intent
        SubscribedRole subRole = new SubscribedRole(protocolRole);

        //Subscribe to protocol
        proxy.subscribeService(zirkId, subRole);
    }

    @Override
    public boolean unsubscribe(ProtocolRole protocolRole) {
        if (protocolRole == null) {
            throw new IllegalArgumentException("Cannot unsubscribe from a null role");
        }

        return proxy.unsubscribe(zirkId, new SubscribedRole(protocolRole));
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location(null)), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        if (event == null) {
            throw new IllegalArgumentException("Cannot send a null event");
        }

        proxy.sendMulticastEvent(zirkId, recipient, event.toJson());
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient,
                          Event event) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        if (event == null) {
            throw new IllegalArgumentException("Cannot send a null event");
        }

        proxy.sendUnicastEvent(zirkId, (BezirkZirkEndPoint) recipient, event.toJson());
    }

    @Override
    public short sendStream(ZirkEndPoint recipient,
                            Stream stream, PipedOutputStream dataStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(zirkId.getZirkId() + streamId, stream.topic);
        BezirkZirkEndPoint recipientSEP = (BezirkZirkEndPoint) recipient;
        proxy.sendStream(zirkId, recipientSEP, stream.toJson(), streamId);
        return streamId;
    }

    @Override
    public short sendStream(ZirkEndPoint recipient, Stream stream, File file) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a stream to a null recipient");
        }

        if (stream == null || stream.topic.isEmpty()) {
            throw new IllegalArgumentException("Null or empty stream specified when sending " +
                    "a file");
        }

        if (file == null) {
            throw new IllegalArgumentException("Cannot send a null file");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("Cannot send file stream because {} is not found: " +
                    file.getName());
        }

        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(zirkId.getZirkId() + streamId, stream.topic);
        proxy.sendStream(zirkId, (BezirkZirkEndPoint) recipient, stream.toJson(), file, streamId);
        return streamId;
    }

    @Override
    public void requestPipeAuthorization(Pipe pipe, PipePolicy allowedIn,
                                         PipePolicy allowedOut, BezirkListener listener) {
        // TODO: Design and implement pipes API
    }

    @Override
    public void getPipePolicy(Pipe pipe,
                              BezirkListener listener) {
        // TODO: Design and implement pipes API
    }

    @Override
    public void discover(RecipientSelector scope,
                         ProtocolRole protocolRole, long timeout,
                         int maxResults, BezirkListener listener) {
        discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;
        dListenerMap.put(zirkId, new DiscoveryBookKeeper(discoveryCount, listener));
        proxy.discover(zirkId, scope, new SubscribedRole(protocolRole), discoveryCount, timeout, maxResults);
    }

    @Override
    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot set a null location");
        } else {
            proxy.setLocation(zirkId, location);
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
