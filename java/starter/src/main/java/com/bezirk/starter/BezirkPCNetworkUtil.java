package com.bezirk.starter;

import com.bezirk.comms.BezirkCommunications;
import com.bezirk.comms.BezirkCommsPC;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.util.BezirkValidatorUtility;
import com.bezrik.network.BezirkNetworkUtilities;
import com.bezrik.network.IntfInetPair;

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
 * Helper class for main zirk to fetch the network interface details.
 *
 * @author ajc6kor
 */
final class BezirkPCNetworkUtil {
    private static final Logger logger = LoggerFactory.getLogger(BezirkPCNetworkUtil.class);

    NetworkInterface fetchNetworkInterface(final BezirkConfig bezirkConfig)
            throws SocketException, NullPointerException, Exception {

        // Resolve the NetworkInterface object for supplied InterfaceName
        NetworkInterface intf = resolveInterface(BezirkCommunications.getINTERFACE_NAME(), bezirkConfig);

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
     * @param bezirkConfig
     * @return
     * @throws SocketException
     * @throws NullPointerException
     */
    private NetworkInterface resolveInterface(final String interfaceName,
                                              final BezirkConfig bezirkConfig) throws SocketException,
            NullPointerException {
        final ServiceStarterHelper serviceStarterHelper = new ServiceStarterHelper();
        // Try to resolve interface for supplied interfaceName
        NetworkInterface intf = NetworkInterface.getByName(interfaceName);

        // If InterfaceName is cannot be resolved, try to automatically
        // detect exactly one "real" (non-loopback) interface to use
        if (intf == null) {
            logger.info("Configured interface " + interfaceName
                    + " could not be resolved. Trying to detect interface.");
            final List<IntfInetPair> interfaces = BezirkNetworkUtilities
                    .getIntfInetPair();
            final int numInf = interfaces.size();

            // If no "real" interface can be found, use the loopback interface
            if (numInf == 0) {
                logger.info("Checking for loopback interface");
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
                logger.info("Detected interface: " + intf.getName());
            }
            // If we find more than one interface AND UI is enabled,
            // prompt the user to choose from available interfaces
            else {
                logger.info("Found multiple interfaces, prompting user to choose ...");
                final String intfName = promptUserForInterface(bezirkConfig);
                if (BezirkValidatorUtility.checkForString(intfName)) {
                    intf = NetworkInterface.getByName(intfName);
                } else {
                    logger.error("Invalid interface name selected! Bezirk is shutting down. . .");
                    System.exit(0);
                }
                logger.info("User chose interface: " + intf.getName());
            }
        } else {
            logger.info("Using configured interface: " + intf.getName());
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
    private String promptUserForInterface(final BezirkConfig bezirkConfig)
            throws SocketException {
        String interfaceName = null;
        if (bezirkConfig.isDisplayEnabled()) {
            final Iterator<IntfInetPair> itr = BezirkNetworkUtilities
                    .getIntfInetPair().iterator();
            final com.bezirk.ethernetconfigui.EthernetConfigurationDialog ethConfigDialog = new com.bezirk.ethernetconfigui.EthernetConfigurationDialog(
                    itr);
            interfaceName = ethConfigDialog.showDialog();
        }
        // If UI is not enabled, prompt user to enter interface via console
        else {
            final Iterator<IntfInetPair> displayIterator = BezirkNetworkUtilities
                    .getIntfInetPair().iterator();
            while (displayIterator.hasNext()) {
                final IntfInetPair pair = displayIterator.next();
                logger.info("Found interface: " + pair.getIntf().getName()
                        + " IP:" + pair.getInet().getHostAddress());
            }
            final Scanner intfScanner = new Scanner(System.in);
            final StringBuilder tempInterfaceName = new StringBuilder(
                    intfScanner.next());
            interfaceName = tempInterfaceName.toString();

            final Iterator<IntfInetPair> itr = BezirkNetworkUtilities
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
     * @return False if the interface was not updated in the properties file
     * @throws Exception
     */
    private boolean updateInterfaceInPropsFile(final NetworkInterface intf) {
        try {
            final Properties properties = BezirkCommsPC.loadProperties();

            // Get the path to the properties file
            final String path = UPADeviceInterface.class.getClassLoader()
                    .getResource(BezirkCommsPC.PROPS_FILE).getPath();
            final FileOutputStream output = new FileOutputStream(path);

            /*
             * Write the interface name to comms.properties if a different
             * Interface was chosen and the config file was not loaded from a
             * jar (as signified by a "!" being in the path)
             */
            if (!intf.getName().equals(BezirkCommsPC.PROPS_FILE)
                    && !path.contains("!")) {
                properties.setProperty("InterfaceName", intf.getName());
                properties.store(output, null);

                logger.info("Updated chosen interface in config file: " + path);
                return true;
            }

            output.close();
            logger.debug("Did not update interface in config file: " + path);
        } catch (FileNotFoundException e) {
            logger.error("Properties file not found in default path.", e);
        } catch (Exception e) {
            logger.error("Exception in storing interface name to properties.",
                    e);

        }
        return false;
    }

}
