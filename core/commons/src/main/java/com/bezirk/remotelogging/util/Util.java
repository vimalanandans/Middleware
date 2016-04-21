/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging.util;

/**
 * Utility class for Logging Module defining all the constants.
 */
public class Util {
    /**
     * Version of the Logging Module.
     */
    public static final String LOGGING_VERSION = "0.0.1";
    /**
     * Constant label for ANY_SPHERE logging
     */
    public static final String ANY_SPHERE = "ALL-SPHERES";
    /**
     * Value for the Control ReceiverField that is sent on the wire.
     */
    public static final String CONTROL_RECEIVER_VALUE = null;

    /**
     * Types of the logged message
     */
    public static enum LOGGING_MESSAGE_TYPE {
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
}
