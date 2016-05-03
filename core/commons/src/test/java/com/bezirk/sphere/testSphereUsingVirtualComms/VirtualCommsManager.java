package com.bezirk.sphere.testSphereUsingVirtualComms;

import com.bezrik.network.BezirkNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Enumeration;


public class VirtualCommsManager {
    private static final Logger logger = LoggerFactory.getLogger(VirtualCommsManager.class);

    public BezirkCommsMock bezirkCommsMock;
    public VirtualDevice[] device = null; //array of VirtualDevice objects.
    private InetAddress inetAddr;

    /**
     * Creates the number of VirtualDevices required for a testcase.
     * It also creates an object of the mock comms (BezirkCommsMock) being used.
     *
     * @param numOfDevices - number of devices being used in a testcase.
     * @throws IOException
     * @throws SQLException
     * @throws Exception
     */
    public void setUp(int numOfDevices) throws IOException, SQLException, Exception {
        getInetAddress();
        bezirkCommsMock = new BezirkCommsMock();

        // Create the VirtualDevice objects
        device = new VirtualDevice[numOfDevices];
        for (int i = 0; i < numOfDevices; i++)
            device[i] = new VirtualDevice(bezirkCommsMock);
    }

    /**
     * Clear the registry of all the devices created.
     *
     * @param numOfDevices -  the number of devices that were created for a testcase.
     */
    public void destroy(int numOfDevices) {
        for (int i = 0; i < numOfDevices; i++)
            device[i].sphereRegistry.clearRegistry();
    }

    InetAddress getInetAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {
                        inetAddr = BezirkNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("Unable to fetch network interface");
        }
        return null;
    }

    /**
     * Prints the spheres and sphereMembership maps of the passed device's registry.
     *
     * @param device - device whose registry contents are to be displayed.
     */
    public void displayRegistryMapsForDevice(VirtualDevice device) {
        logger.info("Spheres map: " + device.sphereRegistry.spheres.toString() + "\n");
        logger.info("sphere membership map: " + device.sphereRegistry.sphereMembership.toString() + "\n");
    }

}
