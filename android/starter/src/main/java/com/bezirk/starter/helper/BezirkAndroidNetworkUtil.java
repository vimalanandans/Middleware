package com.bezirk.starter.helper;

import android.net.wifi.WifiManager;

import com.bezirk.comms.BezirkCommunications;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;
import com.bezrik.network.BezirkNetworkUtilities;
import com.bezrik.network.IntfInetPair;

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
 */
public final class BezirkAndroidNetworkUtil {
    private static final Logger logger = LoggerFactory.getLogger(BezirkAndroidNetworkUtil.class);

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
            networkInterface = NetworkInterface.getByName(BezirkCommunications.getINTERFACE_NAME());
            inetAddress = BezirkValidatorUtility.isObjectNotNull(networkInterface) ? BezirkNetworkUtilities.getIpForInterface(networkInterface) : null;
            if (inetAddress == null) {
                logger.error("Could not resolve ip - Check InterfaceName in preferences.xml");
                logger.error("Possible interface and ip pairs are:");
                printInfInetPairs();
                logger.error("SHUTTING DOWN BEZIRK");
                service.onDestroy();
            }
        } catch (SocketException e) {
            logger.error("Could not find Interface - SHUTTING DOWN BEZIRK", e);
            service.onDestroy();

        }
        return inetAddress;
    }

    private void printInfInetPairs() {
        for (IntfInetPair pair : BezirkNetworkUtilities.getIntfInetPair()) {

            logger.error("Interface: " + pair.getIntf().getName() + " IP:" + pair.getInet().getHostAddress());
        }
    }

}