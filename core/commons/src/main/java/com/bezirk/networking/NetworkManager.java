package com.bezirk.networking;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class NetworkManager {
    private static final Logger logger = LoggerFactory.getLogger(NetworkManager.class);
    protected static final String NETWORK_INTERFACE_NAME_KEY = "NETWORK_INTERFACE_PREFERENCE";
    protected static final String NETWORK_INTERFACE_DEFAULT_VALUE = "";

    private NetworkInterface curInterface = null;

    public List<IntfInetPair> getIntfInetPair() {
        ArrayList<IntfInetPair> list = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() /*&& inetAddress.isSiteLocalAddress()*/) {
                        list.add(new IntfInetPair(intf, inetAddress));
                    }

                }
            }
        } catch (Exception ex) {
            logger.error("Error occurred while getting IntfInetPair \n", ex);
        }
        return list;
    }

    public InetAddress getIpForInterface(NetworkInterface intf) {
        if (intf == null) {
            logger.debug("Input interface is null");
            return null;
        }
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface curIntf = en.nextElement();

                if (intf.getDisplayName().equals(curIntf.getDisplayName())) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                            logger.debug("IP address determined: " + inetAddress.getHostAddress());
                            curInterface = intf;
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error occurred while getting IpForInterface \n", ex);
        }
        return null;
    }


    public InetAddress getLocalInet() {
        getInetAddress();
        if (curInterface == null) {
            getInetAddress();
        }
        InetAddress inetAddress = null;
        for (Enumeration<InetAddress> enumIpAddr = curInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
            logger.debug("enumIpAddr");
             inetAddress = enumIpAddr.nextElement();
            logger.debug("inetaddress is "+inetAddress.getHostAddress());
            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                logger.info("IP address determined: " + inetAddress.getHostAddress());
                return inetAddress;
            }
        }
        return inetAddress;
    }


    /**
     * Gets a MAC address of a local network interface that is not a loopback interface
     *
     * @return Local MAC address
     */
    public byte[] getLocalMACAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        //                        Log.i(TAG,"MAC address determined: " + Hex.encodeToString(intf.getHardwareAddress()));
                        return intf.getHardwareAddress();
                    }

                }
            }
        } catch (Exception ex) {
            logger.error("Error occurred while getting LocalMACAddress \n", ex);
        }
        return null;
    }

    public BezirkZirkEndPoint getServiceEndPoint(ZirkId zirkId) {
        logger.debug("getServiceEndPoint");
        BezirkZirkEndPoint sep = new BezirkZirkEndPoint(zirkId);
        sep.device = getLocalInet().getHostAddress();
        return sep;
    }

    public String getDeviceIp() {
        logger.debug("getDeviceIp");
        return getLocalInet().getHostAddress();
    }

    public abstract String getStoredInterfaceName();

    public abstract void setStoredInterfaceName(String interfaceName);

    public abstract InetAddress getInetAddress();


}
