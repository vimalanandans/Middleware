package com.bezirk.pubsubbroker;

import com.bezirk.control.messages.EventLedger;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;

/**
 * Platform independent API's used by the SADL for handling incoming event pub sub Reception.
 * Also get the stream messages. todo rename the class to ISadleCommsMessageReceiver
 */
public interface SadlEventReceiver {

    /**
     * Comms layer triggers this to process the incoming message
     * on valid message sadl distributes to respective zirk
     *
     * @param eLedger - zirkId of the recipient
     * @return true if the event is processed
     */
    public boolean processEvent(final EventLedger eLedger);
    /**
     * Checks if the recipient has subscribed for this topic. O
     * @param topic - Topic of the incoming Event
     * @param - recipient - zirkId of the recipient
     * @return true if topic is subscribed, false otherwise.
     */
    // Not used anymore
    /*public boolean checkUnicastEvent(final String topic, final ZirkId recipient);
	/**
	 * Checks if Event has any subscribers and returns the list. If location is null, default location is considered and matched.
	 * @param topic - Topic of the incoming Event
	 * @param location - Location of the intended Zirk
	 * @return set of ZirkId that the event has subscribed, null otherwise.
	 */
    // Not used anymore
	/*public Set<ZirkId> checkMulticastEvent(final String topic, final Location location);*/


    /**
     * notify the stream status
     */
    public boolean processStreamStatus(StreamStatusMessage streamStatusNotification);

    /**
     * notify the stream data
     */
    public boolean processNewStream(StreamIncomingMessage streamCallbackMessage);

}
