/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bosch.upa.uhu.remotelogging;
/**
 * Class that gives the status of the logging. {@link false} by default. The Event Sender/Receivers Threads and
 * Control Sender/Receivers Threads check the status of the logging and if true, will send the 
 * {@link UhuLoggingMessage} on the wire.
 */
public final class LoggingStatus {
	/**
	 * Flag that stores the Logging status.
	 */
	private static boolean isLoggingEnabled = false;

	/**
	 * Private constructor to make it a Utility class
	 */
	private LoggingStatus() {}
	/**
	 * returns the status of the logging flag for the stack.
	 * @return true if logging is enabled, false other wise.
	 */
	public static boolean isLoggingEnabled() {
		return isLoggingEnabled;
	}

	/**
	 * sets the Logging flag. This method will be invoked by the ControlReceiverThread after receiving a valid
	 * LoggingServiceMessage
	 * @param isLoggingEnabled a boolean value depending on the status of the LoggingServiceMessage
	 */
	public static void setLoggingEnabled(boolean isLoggingEnabled) {
		LoggingStatus.isLoggingEnabled = isLoggingEnabled;
	}
	
}
