package com.bezirk.comms;

import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;


/**
 * Created by Vimal on 5/19/2015.
 * Interface for Comms layer to dispatch to respective component
 */
public interface IMessageDispatcher {

    /**
     * register control Message receivers
     */
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver);

    // currently sadl consumes all the zirk message. hence no registration
    // if needed extend similar mechanism to control message dispatching


    /**
     * dispatch the control message
     */
    public boolean dispatchServiceMessages(EventLedger eLedger);

    /**
     * dispatch the control message using ledger
     */
    public boolean dispatchControlMessages(ControlLedger tcMessage);

    /**
     * dispatch the control message using ControlMessage and msg
     */
    public boolean dispatchControlMessages(ControlMessage ctrlMsg, String msg);


}
