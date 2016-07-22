package com.bezirk.middleware.proxy;

import com.bezirk.proxy.ProxyServer;
import com.bezirk.proxy.messagehandler.ServiceMessageHandler;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.datastorage.ProxyPersistence;
import com.bezirk.datastorage.ProxyRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.ServiceRegistrationUtil;
import com.bezirk.starter.MainService;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;

public class Proxy implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);

    protected final HashMap<ZirkId, HashSet<BezirkListener>> sidMap = new HashMap<ZirkId, HashSet<BezirkListener>>();
    protected final HashMap<String, HashSet<BezirkListener>> eventListenerMap = new HashMap<String, HashSet<BezirkListener>>();
    protected final HashMap<String, HashSet<BezirkListener>> streamListenerMap = new HashMap<String, HashSet<BezirkListener>>();
    protected final HashMap<String, String> activeStreams = new HashMap<String, String>();
    private final ProxyServer proxy;
    private final ProxyUtil proxyUtil;
    private final ProxyPersistence proxyPersistence;
    private final MainService mainService;
    // StreamDescriptor
    private short streamFactory = 0;
    private ProxyRegistry proxyRegistry = null;

    private ZirkId zirkId;

    public Proxy() {
        proxy = new ProxyServer();
        proxyUtil = new ProxyUtil();
        mainService = new MainService(proxy, null);
        final BroadcastReceiver brForService = new BRForService(activeStreams,
                eventListenerMap, sidMap, streamListenerMap);
        ServiceMessageHandler bezirkPcCallback = new ServiceMessageHandler(brForService);
        mainService.startStack(bezirkPcCallback);
        proxyPersistence = mainService.getBezirkProxyPersistence();
        try {
            proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
        } catch (Exception e) {
            logger.error("Error loading ProxyRegistry", e);
            System.exit(-1);
        }
    }

    public boolean registerZirk(String zirkName) {
        logger.trace("inside RegisterService");
        if (zirkName == null) {
            throw new IllegalArgumentException("Cannot register a Zirk with a null name");
        }

        String zirkIdAsString = proxyRegistry.getBezirkServiceId(zirkName);

        if (null == zirkIdAsString) {
            zirkIdAsString = ServiceRegistrationUtil.generateUniqueServiceID() + ":" + zirkName;
            proxyRegistry.updateBezirkZirkId(zirkName, zirkIdAsString);
            try {
                proxyPersistence.persistBezirkProxyRegistry();
            } catch (Exception e) {
                logger.error("Error in persisting the information", e);
            }
        }

        if (logger.isDebugEnabled()) logger.debug("Zirk-Id: {}", zirkIdAsString);

        zirkId = new ZirkId(zirkIdAsString);
        // Register with Bezirk
        proxy.registerZirk(zirkId, zirkName);
        return true;
    }

    @Override
    public void unregisterZirk() {
        // Clear the Persistence by removing the ZirkId of the unregistering Zirk
        ZirkId sId = (ZirkId) zirkId;
        proxyRegistry.deleteBezirkZirkId(sId.getZirkId());
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

        if (ValidatorUtility.isObjectNotNull(protocolRole.getEventTopics())) {
            proxyUtil.addTopicsToMaps(zirkId, protocolRole.getEventTopics(),
                    listener, sidMap, eventListenerMap, "Event");
        } else {
            logger.info("No Events to Subscribe");
        }
        if (ValidatorUtility.isObjectNotNull(protocolRole.getStreamTopics())) {

            proxyUtil.addTopicsToMaps(zirkId, protocolRole.getStreamTopics(),
                    listener, sidMap, streamListenerMap, "StreamDescriptor");

        } else {
            logger.info("No Streams to Subscribe");
        }

        //Subscribe to protocol
        proxy.subscribeService(zirkId, protocolRole);
    }

    @Override
    public boolean unsubscribe(ProtocolRole protocolRole) {
        if (protocolRole == null) {
            throw new IllegalArgumentException("Cannot unsubscribe from a null role");
        }

        return proxy.unsubscribe(zirkId, protocolRole);
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

        proxy.sendMulticastEvent(zirkId, recipient, event);
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

        proxy.sendUnicastEvent(zirkId, (BezirkZirkEndPoint) recipient, event);
    }

    @Override
    public short sendStream(ZirkEndPoint recipient,
                            StreamDescriptor streamDescriptor, PipedOutputStream dataStream) {
        throw new UnsupportedOperationException("Calling sendStream with a PipedOutputStream is current unimplemented.");
    }

    @Override
    public short sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a streamDescriptor to a null recipient");
        }

        if (streamDescriptor == null || streamDescriptor.topic.isEmpty()) {
            throw new IllegalArgumentException("Null or empty streamDescriptor specified when sending " +
                    "a file");
        }

        if (file == null) {
            throw new IllegalArgumentException("Cannot send a null file");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("Cannot send file streamDescriptor because {} is not found: " +
                    file.getName());
        }

        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(zirkId.getZirkId() + streamId, streamDescriptor.topic);
        proxy.sendStream(zirkId, (BezirkZirkEndPoint) recipient, streamDescriptor, file, streamId);
        return streamId;
    }

    @Override
    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot set a null location");
        } else {
            proxy.setLocation(zirkId, location);
        }
    }
}
