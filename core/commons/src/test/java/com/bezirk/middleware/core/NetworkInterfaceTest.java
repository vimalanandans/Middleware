package com.bezirk.middleware.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecurityPermission;
import java.util.Enumeration;

public class NetworkInterfaceTest {

    public static void main(String[] args) throws Exception {

        SecurityManager mgr = System.getSecurityManager();
        /*
		SecurityManager mgr = new SecurityManager();
		System.setSecurityManager(mgr);
		*/

        try {
            if (mgr == null) {
                System.out.println("Security manager not set");
            } else {
                mgr.checkPermission(new SecurityPermission("getNetworkInformation"));
                System.out.println("Network permission ENABLED ");
            }
        } catch (SecurityException e) {
            System.out.println("Network permission NOT enabled");
        }


        // Iterate through all interfaces on node
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {

            // Work on next interface
            NetworkInterface intf = interfaces.nextElement();
            System.out.println("printing addresses for interface: " + intf + " ...");

            // Check to see if we can get addresses for this interface
            Enumeration<InetAddress> addresses = intf.getInetAddresses();

            // Iterate through addresses for this interface
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!address.isLoopbackAddress()) {
                    System.out.println("    interface has address: " + address.getHostAddress());
                }
            }
        }
    }
}
