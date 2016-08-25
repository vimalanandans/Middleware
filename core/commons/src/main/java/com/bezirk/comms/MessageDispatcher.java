package com.bezirk.comms;

import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;


/**
 * Interface for Comms layer to dispatch to respective component
 */
public interface MessageDispatcher {

    /**
     * register control Message receivers
     */
    boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver);

    // currently PubSubBroker consumes all the zirk message. hence no registration
    // if needed extend similar mechanism to control message dispatching


    /**
     * dispatch the control message
     */
    boolean dispatchServiceMessages(EventLedger eLedger);


    /**
     * dispatch the control message using ControlMessage and msg
     */
    boolean dispatchControlMessages(ControlMessage ctrlMsg, String msg);


}
