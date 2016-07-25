package com.bezirk.componentManager;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.control.messages.MessageLedger;
import com.bezirk.datastorage.ProxyPersistence;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.device.Device;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.proxy.ProxyServer;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.starter.NetworkUtil;
import com.bezirk.streaming.StreamManager;
import com.bezrik.network.NetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * This class is used for
 * <ul>
 * <li>Managing Bezirk lifecycle, eg. create, start, pause, stop, destroy, etc</li>
 * <li>Injecting dependencies among various components</li>
 * <li>Launching Bezirk with some pre-defined configurations regarding which components to be initialized and injected</li>
 * </ul>
 */
public class ComponentManager {

    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
    private final Comms comms;
    private final InetAddress addr;
    private final NetworkUtil networkUtil;
    private final PubSubBroker pubSubBroker;
    private RegistryStorage registryStorage;
    private final Device device;
    private final ProxyServer proxyServer;
    //private final StreamManager streamManager;

    public ComponentManager(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        networkUtil = new NetworkUtil();
        NetworkInterface intf = null;
        try {
            intf = networkUtil.fetchNetworkInterface();
        } catch (Exception e) {
            logger.error("Error in fetching interface name", e);
            System.exit(0);
        }
        addr = NetworkUtilities.getIpForInterface(intf);

        try {
            registryStorage = new RegistryStorage(new DatabaseConnectionForJava("."), "0.0.3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO: Add device initialization to constructor instead of initDevice
        device = new Device();
        device.initDevice();

        pubSubBroker = new PubSubBroker(registryStorage, device);
        proxyServer.setPubSubBrokerService(pubSubBroker);
        //streamManager = new StreamManager(null, pubSubBroker, "");
        comms = new ZyreCommsManager(null, addr, pubSubBroker, null, null, null);
        comms.startComms();
    }

    public ProxyPersistence getBezirkProxyPersistence() {
        return registryStorage;
    }

}
