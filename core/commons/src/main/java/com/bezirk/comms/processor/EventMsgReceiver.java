package com.bezirk.comms.processor;

import com.bezirk.control.messages.EventLedger;

/**
 * event msg receiver to process the incoming event messages
 * */
public interface EventMsgReceiver {

    /**
     * Comms layer triggers this to process the incoming message
     * on valid message sadl distributes to respective zirk
     *
     * @param eLedger - zirkId of the recipient
     * @return true if the event is processed
     */
    boolean processEvent(final EventLedger eLedger);
}
