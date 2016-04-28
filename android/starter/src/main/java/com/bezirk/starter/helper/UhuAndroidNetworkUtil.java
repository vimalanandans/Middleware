package com.bezirk.starter.helper;

import android.net.wifi.WifiManager;

import com.bezirk.comms.BezirkComms;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;
import com.bezrik.network.IntfInetPair;
import com.bezrik.network.UhuNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Helper class used by MainService to fetch the network interface
 * of android device
 * <p/>
 * Created by AJC6KOR on 9/8/2015.
 */
public final class UhuAndroidNetworkUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuAndroidNetworkUtil.class);

    /**
     * Fetch IP address using wifi connection information
     *
     * @param wifi
     * @return
     */
    String getIpAddress(WifiManager wifi) throws UnknownHostException {
        //refer http://stackoverflow.com/questions/16730711/get-my-wifi-ip-address-android
        int ipAddress = wifi.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        return InetAddress.getByAddress(ipByteArray).getHostAddress();
    }

    /**
     * Fetch InetAddress from network interface
     *
     * @param service
     * @return
     */
    InetAddress fetchInetAddress(MainService service) {
        //Get InetAddress from Network Interface
        NetworkInterface networkInterface;
        InetAddress inetAddress = null;
        try {
            networkInterface = NetworkInterface.getByName(BezirkComms.getINTERFACE_NAME());
            inetAddress = BezirkValidatorUtility.isObjectNotNull(networkInterface) ? UhuNetworkUtilities.getIpForInterface(networkInterface) : null;
            if (inetAddress == null) {
                LOGGER.error("Could not resolve ip - Check InterfaceName in preferences.xml");
                LOGGER.error("Possible interface and ip pairs are:");
                printInfInetPairs();
                LOGGER.error("SHUTTING DOWN UHU");
                service.onDestroy();
            }
        } catch (SocketException e) {
            LOGGER.error("Could not find Interface - SHUTTING DOWN UHU", e);
            service.onDestroy();

        }
        return inetAddress;
    }

    private void printInfInetPairs() {
        for (IntfInetPair pair : UhuNetworkUtilities.getIntfInetPair()) {

            LOGGER.error("Interface: " + pair.getIntf().getName() + " IP:" + pair.getInet().getHostAddress());
        }
    }

}