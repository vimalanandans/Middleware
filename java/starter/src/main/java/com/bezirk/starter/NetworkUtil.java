package com.bezirk.starter;

import com.bezirk.util.ValidatorUtility;
import com.bezrik.network.NetworkUtilities;
import com.bezrik.network.IntfInetPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class for main zirk to fetch the network interface details.
 *
 * @author ajc6kor
 */
public final class NetworkUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    public NetworkInterface fetchNetworkInterface()
            throws SocketException, NullPointerException {

        JavaNetworkInterfacePreference networkInterfacePreference = new JavaNetworkInterfacePreference();

        // Resolve the NetworkInterface object for supplied InterfaceName
        NetworkInterface networkInterface =
                resolveInterface(networkInterfacePreference.getStoredInterfaceName());

        networkInterfacePreference.setStoredInterfaceName(networkInterface.getName());

        return networkInterface;
    }

    /**
     * Returns a Network interface for the provided interface name. If the
     * interface supplied cannot be resolved, we try to choose one by following
     * these steps: <li>Find all interfaces on the system. <li>If there is
     * exactly one interface found, use it <li>If more than one are found,
     * prompt the user to enter one <li>If none are found, use the loopback
     * interface
     *
     * @param interfaceName
     * @return
     * @throws SocketException
     * @throws NullPointerException
     */
    private NetworkInterface resolveInterface(final String interfaceName
    ) throws SocketException,
            NullPointerException {
        final ServiceStarterHelper serviceStarterHelper = new ServiceStarterHelper();
        // Try to resolve interface for supplied interfaceName
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);

        // If InterfaceName is cannot be resolved, try to automatically
        // detect exactly one "real" (non-loopback) interface to use
        if (networkInterface == null) {
            logger.info("Configured interface {} could not be resolved. " +
                    "Trying to detect interface.", interfaceName);
            final List<IntfInetPair> interfaces = NetworkUtilities
                    .getIntfInetPair();
            final int numInf = interfaces.size();

            // If no "real" interface can be found, use the loopback interface
            if (numInf == 0) {
                logger.info("Checking for loopback interface");
                networkInterface = NetworkInterface.getNetworkInterfaces().nextElement();

                if (networkInterface != null && networkInterface.isLoopback()) {
                    serviceStarterHelper
                            .fail("Found loopback interface only. Bezirk requires a non-loopback interface for multicast. Exiting now.",
                                    null);
                } else {
                    serviceStarterHelper.fail(
                            "Could not find a loopback interface to use", null);
                }
            }
            // If exactly one real interface can be found, use it
            else if (numInf == 1) {
                final String name = interfaces.get(0).getIntf().getName();
                networkInterface = NetworkInterface.getByName(name);
                logger.info("Detected interface: " + networkInterface.getName());
            }
            // If we find more than one interface AND UI is enabled,
            // prompt the user to choose from available interfaces
            else {
                logger.info("Found multiple interfaces, prompting user to choose ...");
                final String intfName = promptUserForInterface();
                if (ValidatorUtility.checkForString(intfName)) {
                    networkInterface = NetworkInterface.getByName(intfName);
                } else {
                    logger.error("Invalid interface name selected! Bezirk is shutting down. . .");
                    System.exit(0);
                }
                logger.info("User chose interface: " + networkInterface.getName());
            }
        } else {
            logger.info("Using configured interface: " + networkInterface.getName());
        }

        return networkInterface;
    }

    /**
     * Prompts the user to enter an interface. If the UI is enabled, pop up a
     * swing window and prompt the user to choose from a pull-down menu. If UI
     * is not enabled, prompt the user to enter the interface on the console
     *
     * @return
     * @throws SocketException
     */
    private String promptUserForInterface() {
        String interfaceName;
        //if (bezirkConfig.isDisplayEnabled()) {
        final Iterator<IntfInetPair> itr = NetworkUtilities
                .getIntfInetPair().iterator();
        final com.bezirk.ui.ethernetconfigui.EthernetConfigurationDialog ethConfigDialog = new com.bezirk.ui.ethernetconfigui.EthernetConfigurationDialog(
                itr);
        interfaceName = ethConfigDialog.showDialog();
        //}
        // If UI is not enabled, prompt user to enter interface via console
//        else {
//            for (IntfInetPair pair : NetworkUtilities.getIntfInetPair()) {
//                logger.info("Found interface: " + pair.getIntf().getName()
//                        + " IP:" + pair.getInet().getHostAddress());
//            }
//            final Scanner intfScanner = new Scanner(System.in);
//            final StringBuilder tempInterfaceName = new StringBuilder(
//                    intfScanner.next());
//            interfaceName = tempInterfaceName.toString();
//
//            final Iterator<IntfInetPair> itr = NetworkUtilities
//                    .getIntfInetPair().iterator();
//            while (itr.hasNext()) {
//                final IntfInetPair pair = itr.next();
//                if (tempInterfaceName.toString() != null
//                        && !tempInterfaceName.toString().isEmpty()
//                        && tempInterfaceName.toString().equals(
//                        pair.getIntf().getName())) {
//                    interfaceName = tempInterfaceName.toString();
//                    break;
//                }
//            }
//            intfScanner.close();
//        }

        return interfaceName;
    }


}
