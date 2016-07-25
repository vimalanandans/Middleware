package com.bezirk.middleware.proxy;

import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SendUnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.datastorage.ProxyPersistence;
import com.bezirk.datastorage.ProxyRegistry;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.ProxyServer;
import com.bezirk.proxy.ServiceRegistrationUtil;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.proxy.messagehandler.ServiceMessageHandler;
import com.bezirk.starter.MainService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProxyClient implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(ProxyClient.class);

    private final Map<ZirkId, Set<BezirkListener>> sidMap = new HashMap<>();
    private final Map<String, Set<BezirkListener>> eventListenerMap = new HashMap<>();
    private final Map<String, Set<BezirkListener>> streamListenerMap = new HashMap<>();
    private final Map<String, String> activeStreams = new HashMap<>();
    private final ProxyServer proxy = new ProxyServer();
    private final ProxyPersistence proxyPersistence;

    private short streamFactory = 0;
    private ProxyRegistry proxyRegistry = null;

    private ZirkId zirkId;

    public ProxyClient() {
        MainService mainService = new MainService(proxy, null);
        final BroadcastReceiver brForService = new ZirkMessageReceiver(activeStreams,
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
        String zirkIdAsString = proxyRegistry.getBezirkServiceId(zirkName);

        if (null == zirkIdAsString) {
            zirkIdAsString = ServiceRegistrationUtil.generateUniqueServiceID() + ":" + zirkName;
            proxyRegistry.updateBezirkZirkId(zirkName, zirkIdAsString);
            try {
                proxyPersistence.persistBezirkProxyRegistry();
            } catch (Exception e) {
                logger.error("Error in persisting the information", e);
                return false;
            }
        }

        if (logger.isTraceEnabled()) logger.trace("Zirk-Id: {}", zirkIdAsString);

        zirkId = new ZirkId(zirkIdAsString);
        proxy.registerZirk(new RegisterZirkAction(zirkId, zirkName));

        return true;
    }

    @Override
    public void unregisterZirk() {
        proxyRegistry.deleteBezirkZirkId(zirkId.getZirkId());
        try {
            proxyPersistence.persistBezirkProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in persisting the information", e);
        }
        proxy.unregister(new RegisterZirkAction(zirkId, null));
    }

    @Override
    public void subscribe(final ProtocolRole protocolRole, final BezirkListener listener) {
        if (protocolRole.getEventTopics() != null) {
            addTopicsToMaps(zirkId, protocolRole.getEventTopics(),
                    listener, sidMap, eventListenerMap, "Event");
        } else {
            logger.trace("No Events to Subscribe");
        }
        if (protocolRole.getStreamTopics() != null) {
            addTopicsToMaps(zirkId, protocolRole.getStreamTopics(),
                    listener, sidMap, streamListenerMap, "StreamDescriptor");
        } else {
            logger.trace("No Streams to Subscribe");
        }

        proxy.subscribeService(new SubscriptionAction(zirkId, protocolRole));
    }

    private void addTopicsToMaps(final ZirkId subscriber, final String[] topics,
                                 final BezirkListener listener, Map<ZirkId, Set<BezirkListener>> sidMap,
                                 Map<String, Set<BezirkListener>> listenerMap, String topicType) {
        for (String topic : topics) {
            if (sidMap.containsKey(subscriber)) {
                sidMap.get(subscriber).add(listener);
            } else {
                Set<BezirkListener> listeners = new HashSet<>();
                listeners.add(listener);
                sidMap.put(subscriber, listeners);
            }

            if (listenerMap.containsKey(topic)) {
                final Set<BezirkListener> zirkList = listenerMap.get(topic);
                if (zirkList.contains(listener)) {
                    logger.warn(topicType + " already registered with the Label " + topic);
                } else {
                    zirkList.add(listener);
                }
            } else {
                Set<BezirkListener> regServiceList = new HashSet<>();
                regServiceList.add(listener);
                listenerMap.put(topic, regServiceList);
            }
        }
    }

    @Override
    public boolean unsubscribe(ProtocolRole protocolRole) {
        return proxy.unsubscribe(new SubscriptionAction(zirkId, protocolRole));
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location(null)), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        proxy.sendMulticastEvent(new SendMulticastEventAction(zirkId, recipient, event));
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        proxy.sendUnicastEvent(new SendUnicastEventAction(zirkId, recipient, event));
    }

    @Override
    public short sendStream(ZirkEndPoint recipient,
                            StreamDescriptor streamDescriptor, PipedOutputStream dataStream) {
        throw new UnsupportedOperationException("Calling sendStream with a PipedOutputStream is current unimplemented.");
    }

    @Override
    public short sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(zirkId.getZirkId() + streamId, streamDescriptor.topic);
        proxy.sendStream(new SendFileStreamAction(zirkId, recipient, streamDescriptor, streamId, file));
        return streamId;
    }

    @Override
    public void setLocation(Location location) {
        proxy.setLocation(new SetLocationAction(zirkId, location));
    }
}
