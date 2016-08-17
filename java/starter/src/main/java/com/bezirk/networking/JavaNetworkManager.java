package com.bezirk.networking;

import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

public class JavaNetworkManager extends NetworkManager {
    private static final Logger logger = LoggerFactory.getLogger(JavaNetworkManager.class);
    private final Preferences preferences;

    public JavaNetworkManager() {
        this.preferences = Preferences.userNodeForPackage(JavaNetworkManager.class);

        try {
            logger.debug("Network preferences initialized successfully");
            logger.debug(NETWORK_INTERFACE_NAME_KEY + " --> " + getStoredInterfaceName());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getStoredInterfaceName() {
        return preferences.get(NETWORK_INTERFACE_NAME_KEY, NETWORK_INTERFACE_DEFAULT_VALUE);
    }

    @Override
    public void setStoredInterfaceName(String interfaceName) {
        try {
            preferences.put(NETWORK_INTERFACE_NAME_KEY, interfaceName);
            preferences.sync();
            logger.debug("Network preferences is stored");
        } catch (BackingStoreException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public InetAddress getInetAddress() {
        NetworkInterface networkInterface = resolveInterface(getStoredInterfaceName());
        if (null != networkInterface) {
            return getIpForInterface(networkInterface);
        }
        return null;
    }

    /**
     * Returns a non-loopback Network interface. If the
     * interface supplied cannot be resolved, network interface is chosen by looking up all available interfaces.
     * <ul>
     * <li>If no interface is found, method returns null</li>
     * <li>If exactly one non-loopback interface is found, the method return that interface</li>
     * <li>If more than one interfaces are found, user is prompted for selection. The selected interface is then returned</li>
     * </ul>
     */
    private NetworkInterface resolveInterface(final String interfaceName) {
        NetworkInterface networkInterface = null;
        try {
            if (null != interfaceName) {
                networkInterface = NetworkInterface.getByName(interfaceName); //resolve interface for supplied interfaceName
            }

            if (null == networkInterface) {
                logger.debug("Configured interface '" + interfaceName + "' could not be resolved. Detecting another interface ...");
                final List<InterfaceInetPair> interfaces = getInterfaceInetPair();
                final int numInf = interfaces.size();

                switch (numInf) {
                    case 0: //no interface found
                        logger.error("Non-loopback interface not found, Bezirk will not be able to send messages to other devices.");
                        return null;
                    case 1: //only 1 interface found
                        networkInterface = interfaces.get(0).getNetworkInterface();
                        logger.debug("Interface selected '" + networkInterface.getName() + "'");
                        break;
                    default:
                        logger.debug(numInf + " interfaces detected, prompting user to choose");
                        final String intfName = promptUserForInterface();
                        if (ValidatorUtility.checkForString(intfName)) {
                            networkInterface = NetworkInterface.getByName(intfName);
                            logger.debug("Interface selected '" + intfName + "'");
                        } else {
                            logger.error("Interface not selected! Bezirk is shutting down. . .");
                            System.exit(0);
                        }
                        break;
                }
            } else {
                logger.info("Using configured interface: " + networkInterface.getName());
            }
        } catch (SocketException se) {
            logger.debug("SocketException while resolving network interface" + se);
        } catch (NullPointerException e) {
            logger.debug("NullPointerException while resolving network interface" + e);
        }
        return networkInterface;
    }

    /**
     * Prompts the user to choose a network interface. Pops up a
     * swing window and prompts the user to choose from a pull-down menu
     */
    private String promptUserForInterface() {
        String interfaceName;
        final Iterator<InterfaceInetPair> itr = getInterfaceInetPair().iterator();
        final EthernetConfigurationDialog ethConfigDialog = new EthernetConfigurationDialog(
                itr);
        interfaceName = ethConfigDialog.showDialog();
        return interfaceName;
    }

    private static class EthernetConfigurationDialog {
        private final Iterator<InterfaceInetPair> iterator;

        public EthernetConfigurationDialog(Iterator<InterfaceInetPair> iterator) {
            this.iterator = iterator;
        }

        public String showDialog() {
            final List<String> temp = new ArrayList<>();
            InterfaceInetPair pair;
            while (iterator.hasNext()) {
                pair = iterator.next();
                temp.add(pair.getNetworkInterface().getName());
            }
            final String[] interfaceNames = temp.toArray(new String[temp.size()]);
            return (String) JOptionPane.showInputDialog(null,
                    "Please select your active network for Bezirk to send and receive messages", "Bezirk Network Selection",
                    JOptionPane.QUESTION_MESSAGE, null, interfaceNames, null);

        }

    }


}
