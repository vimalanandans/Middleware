package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.control.messages.ControlMessage;

// who ever wishes to receive the control message
// registers the respective control message Discriminator type
public interface    CtrlMsgReceiver {
    // interface to process control message.
    boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg);
}
