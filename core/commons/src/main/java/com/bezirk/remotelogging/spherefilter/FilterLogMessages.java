/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging.spherefilter;

import java.util.List;

import com.bezirk.remotelogging.util.Util;

/**
 * This class filters the log messages that are sent to the Logging Service.
 * It checks if the Event/ Control Message that is sent on the wire belongs to the sphere in
 * which Logging Service is interested and filters the message accordingly.
 */
public final class FilterLogMessages {

	/**
	 * List storing the spheres in which Logging Service is Activated.
	 * Ex. [Home, Garage]
	 */
	private static List<String> loggingSphereList = null;
	/**
	 * ANY_SPHERE flag
	 */
	private static boolean anySphereEnabled = false;

	/**
	 * Private Constructor in order make this a Utility class.
	 */
	private FilterLogMessages() {}
	/**
	 * updates the loggingSphereList with spheres selected by the logging service.
	 * <p>
	 * This method checks if the list contains only ANY_SPHERE mode and if set sets the anySphereEnabled
	 * Flag to true.
	 * @param list list of spheres for which Logging Service is activated.
	 */
	public static void setLoggingSphereList(final List<String> list){
		loggingSphereList = list;
		if(list.size() == 1){
			anySphereEnabled = list.get(0).equals(Util.ANY_SPHERE);
		}
	}

	/**
	 * Checks if the Control/ Event Message that is sent on the wire belongs to the sphere in which 
	 * Logging Service is activated
	 * @param sphereId sphereId of the control/ Event Message
	 * @return true if Logging Service is activated for ANY_SPHERE or if the message belongs to the
	 * sphere in which Logging service is activated. False, otherwise.
	 */
	public static boolean checkSphere(final String sphereId){
		if(loggingSphereList == null){
			return false;
		}
		else if(anySphereEnabled){
			return true;
		}
		return loggingSphereList.contains(sphereId);
	}

}
