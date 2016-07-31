package com.bezirk.pubsubbroker;

import com.bezirk.actions.ReceiveFileStreamAction;

/**
 * Platform independent API's used by the pubsuber for handling incoming event pub sub Reception.
 * Also get the stream messages.
 */
public interface PubSubEventReceiver {

    /**
     * Comms layer triggers this to process the incoming message
     * on valid message sadl distributes to respective zirk
     *
     * @param eLedger - zirkId of the recipient
     * @return true if the event is processed
     */
   // boolean processEvent(final EventLedger eLedger); // moved this functionalites to EventMsgReceiver
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
     * notify the stream data
     */
    boolean processNewStream(ReceiveFileStreamAction streamCallbackMessage);

}
