package com.bezirk.remotelogging;

import com.bezirk.comms.BezirkComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;


/**
 * Created by Vimal on 7/11/2016.
 * Interface for the remote logging feature
 *  TODO: make it as component
 */
public interface RemoteMessageLog {

    /**
     * Constant label for ALL_SPHERES logging.
     * TODO : Used in GUI selection display and internal modules. Review it
     */
    public static final String ALL_SPHERES = "ALL-SPHERES";

    /** initialize the remote logging module. */
    public boolean initRemoteLogger(BezirkComms comms);

    /** set logger to enable or disable **/
    public boolean setLogger(boolean enable, String[] sphereName);

    /** is logging enabled. returns true if it is enabled */
    public boolean isEnabled();

    /** send the control ledger to logging */
    public boolean sendRemoteLogMessage(ControlLedger tcMessage) ;

    /** send the control message to logging */
    public boolean sendRemoteLogMessage(ControlMessage msg) ;

    public boolean isRemoteMessageValid(RemoteLoggingMessage logMessage);
}
