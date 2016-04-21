package com.bezirk.starter;

import com.bezirk.comms.UhuComms;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.util.UhuValidatorUtility;
import com.bezrik.network.IntfInetPair;
import com.bezrik.network.UhuNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Helper class for main service to fetch the network interface details.
 *
 * @author ajc6kor
 */
final class UhuPCNetworkUtil {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UhuPCNetworkUtil.class);

    NetworkInterface fetchNetworkInterface(final UhuConfig uhuConfig)
            throws SocketException, NullPointerException, Exception {

        NetworkInterface intf = null;

        // Resolve the NetworkInterface object for supplied InterfaceName
        intf = resolveInterface(UhuComms.getINTERFACE_NAME(), uhuConfig);

        // If we chose a different Interface than what is written in the
        // config file and the config file is writable (it is not in a jar),
        // then write the new interface name to the config file
        updateInterfaceInPropsFile(intf);

        return intf;
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
     * @param uhuConfig
     * @return
     * @throws SocketException
     * @throws NullPointerException
     */
    private NetworkInterface resolveInterface(final String interfaceName,
                                              final UhuConfig uhuConfig) throws SocketException,
            NullPointerException {
        final ServiceStarterHelper serviceStarterHelper = new ServiceStarterHelper();
        // Try to resolve interface for supplied interfaceName
        NetworkInterface intf = NetworkInterface.getByName(interfaceName);

        // If InterfaceName is cannot be resolved, try to automatically
        // detect exactly one "real" (non-loopback) interface to use
        if (intf == null) {
            LOGGER.info("Configured interface " + interfaceName
                    + " could not be resolved. Trying to detect interface.");
            final List<IntfInetPair> interfaces = UhuNetworkUtilities
                    .getIntfInetPair();
            final int numInf = interfaces.size();

            // If no "real" interface can be found, use the loopback interface
            if (numInf == 0) {
                LOGGER.info("Checking for loopback interface");
                intf = NetworkInterface.getNetworkInterfaces().nextElement();

                if (intf != null && intf.isLoopback()) {
                    serviceStarterHelper
                            .fail("Found loopback interface only. UhU requires a non-loopback interface for multicast. Exiting now.",
                                    null);
                } else {
                    serviceStarterHelper.fail(
                            "Could not find a loopback interface to use", null);
                }
            }
            // If exactly one real interface can be found, use it
            else if (numInf == Integer.valueOf(1)) {
                final String intfName = interfaces.get(0).getIntf().getName();
                intf = NetworkInterface.getByName(intfName);
                LOGGER.info("Detected interface: " + intf.getName());
            }
            // If we find more than one interface AND UI is enabled,
            // prompt the user to choose from available interfaces
            else {
                LOGGER.info("Found multiple interfaces, prompting user to choose ...");
                final String intfName = promptUserForInterface(uhuConfig);
                if (UhuValidatorUtility.checkForString(intfName)) {
                    intf = NetworkInterface.getByName(intfName);
                } else {
                    LOGGER.error("Invalid interface name selected! Uhu is shutting down. . .");
                    System.exit(0);
                }
                LOGGER.info("User chose interface: " + intf.getName());
            }
        } else {
            LOGGER.info("Using configured interface: " + intf.getName());
        }

        return intf;
    }

    /**
     * Prompts the user to enter an interface. If the UI is enabled, pop up a
     * swing window and prompt the user to choose from a pull-down menu. If UI
     * is not enabled, prompt the user to enter the interface on the console
     *
     * @return
     * @throws SocketException
     */
    private String promptUserForInterface(final UhuConfig uhuConfig)
            throws SocketException {
        String interfaceName = null;
        if (uhuConfig.isDisplayEnabled()) {
            final Iterator<IntfInetPair> itr = UhuNetworkUtilities
                    .getIntfInetPair().iterator();
            final com.bezirk.ethernetconfigui.EthernetConfigurationDialog ethConfigDialog = new com.bezirk.ethernetconfigui.EthernetConfigurationDialog(
                    itr);
            interfaceName = ethConfigDialog.showDialog();
        }
        // If UI is not enabled, prompt user to enter interface via console
        else {
            final Iterator<IntfInetPair> displayIterator = UhuNetworkUtilities
                    .getIntfInetPair().iterator();
            while (displayIterator.hasNext()) {
                final IntfInetPair pair = displayIterator.next();
                LOGGER.info("Found interface: " + pair.getIntf().getName()
                        + " IP:" + pair.getInet().getHostAddress());
            }
            final Scanner intfScanner = new Scanner(System.in);
            final StringBuilder tempInterfaceName = new StringBuilder(
                    intfScanner.next());
            interfaceName = tempInterfaceName.toString();

            final Iterator<IntfInetPair> itr = UhuNetworkUtilities
                    .getIntfInetPair().iterator();
            while (itr.hasNext()) {
                final IntfInetPair pair = itr.next();
                if (tempInterfaceName.toString() != null
                        && !tempInterfaceName.toString().isEmpty()
                        && tempInterfaceName.toString().equals(
                        pair.getIntf().getName())) {
                    interfaceName = tempInterfaceName.toString();
                    break;
                }
            }
            intfScanner.close();
        }

        return interfaceName;
    }

    /**
     * Update the comms.properties file with the chosen interface if possible.
     * This is not possible if comms.properties is in a jar, in which case we do
     * nothing and return false
     *
     * @param intf
     * @param uhuConfig
     * @return False if the interface was not updated in the properties file
     * @throws Exception
     */
    private boolean updateInterfaceInPropsFile(final NetworkInterface intf) {
        try {
            final Properties properties = com.bezirk.comms.UhuCommsPC.loadProperties();

            // Get the path to the properties file
            final String path = UPADeviceInterface.class.getClassLoader()
                    .getResource(com.bezirk.comms.UhuCommsPC.PROPS_FILE).getPath();
            final FileOutputStream output = new FileOutputStream(path);

            /*
             * Write the interface name to comms.properties if a different
             * Interface was chosen and the config file was not loaded from a
             * jar (as signified by a "!" being in the path)
             */
            if (!intf.getName().equals(com.bezirk.comms.UhuCommsPC.PROPS_FILE)
                    && !path.contains("!")) {
                properties.setProperty("InterfaceName", intf.getName());
                properties.store(output, null);

                LOGGER.info("Updated chosen interface in config file: " + path);
                return true;
            }

            output.close();
            LOGGER.debug("Did not update interface in config file: " + path);
        } catch (FileNotFoundException e) {

            LOGGER.error("Properties file not found in default path.", e);

        } catch (Exception e) {

            LOGGER.error("Exception in storing interface name to properties.",
                    e);

        }
        return false;
    }

}
