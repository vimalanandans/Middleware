package com.bosch.upa.uhu.comms;

import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.EventLedger;


/**
 * Created by Vimal on 5/19/2015.
 * Interface for Comms layer to dispatch to respective component
 */
public interface IMessageDispatcher {

    /** register control Message receivers */
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, ICtrlMsgReceiver receiver);

    // currently sadl consumes all the service message. hence no registration
    // if needed extend similar mechanism to control message dispatching


    /** dispatch the control message */
    public boolean dispatchServiceMessages(EventLedger eLedger);

    /** dispatch the control message using ledger */
    public boolean dispatchControlMessages(ControlLedger tcMessage);

    /** dispatch the control message using ControlMessage and msg */
    public boolean dispatchControlMessages(ControlMessage ctrlMsg, String msg);


}
