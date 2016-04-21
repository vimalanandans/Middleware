package com.bezirk.comms;

import com.bezirk.control.messages.ControlMessage;

/**
 * Created by vnd2kor on 5/18/2015.
 */
// Sadl, Sphere Logger,...who ever wishes to receive the message
// registers the respective control message Discriminator type
public interface ICtrlMsgReceiver {
    // interface to process control message.
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg);

}
