/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.networking;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class NetworkManager {
    private static final Logger logger = LoggerFactory.getLogger(NetworkManager.class);
    protected static final String NETWORK_INTERFACE_NAME_KEY = "NETWORK_INTERFACE_PREFERENCE";
    protected static final String NETWORK_INTERFACE_DEFAULT_VALUE = "";

    private NetworkInterface curInterface = null;

    public List<InterfaceInetPair> getInterfaceInetPair() {
        ArrayList<InterfaceInetPair> list = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        list.add(new InterfaceInetPair(networkInterface, inetAddress));
                    }

                }
            }
        } catch (SocketException e) {
            logger.error("Error occurred while getting InterfaceInetPair", e);
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
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() &&
                                inetAddress.isSiteLocalAddress()) {
                            logger.debug("IP address determined: " + inetAddress.getHostAddress());
                            curInterface = intf;
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("Error occurred while getting IpForInterface", e);
        }
        return null;
    }


    public InetAddress getLocalInet() {
        if (curInterface == null) {
            getInetAddress();
        }
        for (Enumeration<InetAddress> enumIpAddr = curInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
            InetAddress inetAddress = enumIpAddr.nextElement();
            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() &&
                    inetAddress.isSiteLocalAddress()) {
                return inetAddress;
            }
        }
        return null;
    }


    /**
     * Gets a MAC address of a local network interface that is not a loopback interface
     *
     * @return Local MAC address
     */
    public byte[] getLocalMACAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() &&
                            inetAddress.isSiteLocalAddress()) {
                        return networkInterface.getHardwareAddress();
                    }

                }
            }
        } catch (SocketException e) {
            logger.error("Error occurred while getting LocalMACAddress", e);
        }
        return null;
    }

    @Deprecated // create using new BezirkZirkEndPoint(comms.getNodeId(), zirkId)
    public BezirkZirkEndPoint getServiceEndPoint(ZirkId zirkId) {
        BezirkZirkEndPoint sep = new BezirkZirkEndPoint(zirkId);
        sep.device = getLocalInet().getHostAddress();
        return sep;
    }

    public String getDeviceIp() {
        return getLocalInet().getHostAddress();
    }

    public abstract String getStoredInterfaceName();

    public abstract void setStoredInterfaceName(String interfaceName);

    public abstract InetAddress getInetAddress();


}
