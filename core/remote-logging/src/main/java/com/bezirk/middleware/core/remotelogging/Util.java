package com.bezirk.middleware.core.remotelogging;

import java.util.List;

/**
 * Utility class for Logging Module defining all the constants.
 */
public final class Util {
    /**
     * Version of the Logging Module.
     */
    public static final String LOGGING_VERSION = "0.0.1";
    /**
     * Value for the Control ReceiverField that is sent on the wire.
     */
    public static final String CONTROL_RECEIVER_VALUE = null;

    /**
     * Types of the logged message
     */
    public enum LOGGING_MESSAGE_TYPE {
        /**
         * Used by the client of Event Sender
         */
        EVENT_MESSAGE_SEND,
        /**
         * Used by the client of Event Receiver
         */
        EVENT_MESSAGE_RECEIVE,
        /**
         * Used by the client of Control Message Sender
         */
        CONTROL_MESSAGE_SEND,
        /**
         * Used by the client of the Control Message Receiver
         */
        CONTROL_MESSAGE_RECEIVE
    }

    /**
     * List storing the spheres in which Logging Zirk is Activated.
     * Ex. [Home, Garage]
     */
    private static List<String> loggingSphereList;
    /**
     * ALL_SPHERES flag
     */
    private static boolean anySphereEnabled = false;

    private Util(){}

    /**
     * Updates the <code>loggingSphereList</code> with spheres selected by the logging Zirk.
     * <p>
     * This method checks if the list contains only <code>ALL_SPHERES</code> mode and if so sets the
     * <code>anySphereEnabled</code> flag to <code>true</code>.
     * </p>
     *
     * @param list list of spheres for which Logging Zirk is activated.
     */
    public static void setLoggingSphereList(final List<String> list) {
        loggingSphereList = list;
        if (list.size() == 1) {
            anySphereEnabled = list.get(0).equals(RemoteLog.ALL_SPHERES);
        }
    }

    /**
     * Checks if the Control/ Event Message that is sent on the wire belongs to the sphere in which
     * Logging Zirk is activated
     *
     * @param sphereId sphereId of the control/ Event Message
     * @return true if Logging Zirk is activated for ALL_SPHERES or if the message belongs to the
     * sphere in which Logging zirk is activated. False, otherwise.
     */
    public static boolean checkSphere(final String sphereId) {
        if (loggingSphereList == null) {
            return false;
        } else if (anySphereEnabled) {
            return true;
        }
        return loggingSphereList.contains(sphereId);
    }
}
