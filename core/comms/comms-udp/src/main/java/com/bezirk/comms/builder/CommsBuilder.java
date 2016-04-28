package com.bezirk.comms.builder;

import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.BezirkCommsManager;

/**
 * Created by vnd2kor on 4/13/2015.
 * This creates the uhu / qmqp / mqtt - comms based configuration and returns
 * at the moment it doesn't store the comms object since we could create multiple comms
 */
public class CommsBuilder {

    static public IUhuComms createComms() {
        IUhuComms comms;

        // for Bezirk (as of now)
        comms = new BezirkCommsManager();

        return comms;
    }
}
