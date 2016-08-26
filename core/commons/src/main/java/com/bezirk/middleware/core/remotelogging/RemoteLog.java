package com.bezirk.middleware.core.remotelogging;


import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.Ledger;

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
     * enable the control logging for middleware message analysing purpose.
     * enableRemoteLogging must be true
     * **/
    boolean enableLogging(boolean enableRemoteLogging, boolean enableControl, boolean enableFileLogging, String[] sphereName);

    /** check whether the remoteLogging is enabled or not */
    boolean isRemoteLoggingEnabled();


    /** to send the incoming event message for logging */
    boolean sendRemoteLogToServer(Ledger ledger) ;

    /** to send the incoming control message for logging */
    boolean sendRemoteLogToServer(ControlMessage message) ;

    //boolean isRemoteMessageValid(RemoteLoggingMessage logMessage);
}
