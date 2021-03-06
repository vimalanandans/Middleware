/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.remotelogging;

import java.util.ArrayList;
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
    private static boolean anySphereEnabled;

    private Util() {
    }

    /**
     * Updates the <code>loggingSphereList</code> with spheres selected by the logging Zirk.
     * <p>
     * This method checks if the list contains only <code>ALL_SPHERES</code> mode and if so sets the
     * <code>anySphereEnabled</code> flag to <code>true</code>.
     * </p>
     *
     * @param list list of spheres for which Logging Zirk is activated.
     */
    public static synchronized void setLoggingSphereList(final List<String> list) {
        loggingSphereList = new ArrayList<>(list);
        if (list.size() == 1) {
            anySphereEnabled = RemoteLog.ALL_SPHERES.equals(list.get(0));
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
