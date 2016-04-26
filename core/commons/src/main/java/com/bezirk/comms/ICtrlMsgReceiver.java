package com.bezirk.comms;

import com.bezirk.control.messages.ControlMessage;

// Sadl, sphere Logger,...who ever wishes to receive the message
// registers the respective control message Discriminator type
public interface ICtrlMsgReceiver {
    // interface to process control message.
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg);
}
