package com.bezirk.remotelogging;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;


/**
 * Created by Vimal on 7/11/2016.
 * Interface for the remote logging feature to the middleware
 */
public interface RemoteLog {

    /**
     * Constant label for ALL_SPHERES logging.
     */
    String ALL_SPHERES = "ALL-SPHERES";

    /**
     * set logger to enable or disable the remote logging
     * This starts the logging server and trigger other nodes client to listen to
     * **/
    boolean enableLogging(boolean enableRemoteLogging, String[] sphereName);

    /** check whether the remoteLogging is enabled or not */
    boolean isRemoteLoggingEnabled();

    /** send the control ledger or event ledger to logging */
    boolean sendRemoteLogLedgerMessage(Ledger ledger) ;

    /** send the control ledger to logging */
    //boolean sendRemoteLogMessage(ControlLedger tcMessage) ;

    /** send the control message to logging */
    boolean sendRemoteLogControlMessage(ControlMessage msg) ;
}
