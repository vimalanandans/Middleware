package com.bezirk.comms;

import com.bezirk.control.messages.ControlMessage;

// who ever wishes to receive the control message
// registers the respective control message Discriminator type
public interface    CtrlMsgReceiver {
    // interface to process control message.
    boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg);
}
