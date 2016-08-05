package com.bezirk.controlui;

import java.util.List;

/**
 * This class filters the logger messages that are sent to the Logging Zirk.
 * It checks if the Event/ Control Message that is sent on the wire belongs to the sphere in
 * which Logging Zirk is interested and filters the message accordingly.
 */
public final class FilterLogMessages {

    /**
     * List storing the spheres in which Logging Zirk is Activated.
     * Ex. [Home, Garage]
     */
    private static List<String> loggingSphereList = null;
    /**
     * ALL_SPHERES flag
     */
    private static boolean anySphereEnabled = false;

    /**
     * Private Constructor in order make this a Utility class.
     */
    private FilterLogMessages() {
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
    public static void setLoggingSphereList(final List<String> list) {
        loggingSphereList = list;
        if (list.size() == 1) {
            anySphereEnabled = list.get(0).equals(RemoteLoggingConfig.ALL_SPHERES);
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
