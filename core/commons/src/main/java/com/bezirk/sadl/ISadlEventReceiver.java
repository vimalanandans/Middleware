package com.bezirk.sadl;

import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.control.messages.EventLedger;

/**
 * Platform independent API's used by the SADL for handling incoming event pub sub Reception.
 * Also get the stream messages. todo rename the class to ISadleCommsMessageReceiver
 */
public interface ISadlEventReceiver {

    /**
     * Comms layer triggers this to process the incoming message
     *  on valid message sadl distributes to respective service
     * @param eLedger - serviceId of the recipient
     * @return true if the event is processed
     */
    public boolean processEvent(final EventLedger eLedger);
	/**
	 * Checks if the recipient has subscribed for this topic. O
	 * @param topic - Topic of the incoming Event
	 * @param - recipient - serviceId of the recipient
	 * @return true if topic is subscribed, false otherwise.
	 */
	// Not used anymore
	/*public boolean checkUnicastEvent(final String topic, final UhuServiceId recipient);
	/**
	 * Checks if Event has any subscribers and returns the list. If location is null, default location is considered and matched.
	 * @param topic - Topic of the incoming Event
	 * @param location - Location of the intended Service
	 * @return set of UhuServiceId that the event has subscribed, null otherwise.
	 */
	// Not used anymore
	/*public Set<UhuServiceId> checkMulticastEvent(final String topic, final Location location);*/


	/** notify the stream status */
	public boolean processStreamStatus(StreamStatusMessage streamStatusNotifciation);

	/** notify the stream data */
	public boolean processNewStream(StreamIncomingMessage streamCallbackMessage);

}
