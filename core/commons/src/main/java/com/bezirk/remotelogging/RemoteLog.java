package com.bezirk.remotelogging;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;


/**
 * Created by Vimal on 7/11/2016.
 * Interface for the remote logging feature to the middleware
 *  TODO: make it as component
 */
public interface RemoteLog {

    /**
     * Constant label for ALL_SPHERES logging.
     * TODO : Used in GUI selection display and internal modules. Review it
     */

    String ALL_SPHERES = "ALL-SPHERES";

    /** Start Logging . By component manager */


    boolean startRemoteLoggingService(final RemoteLoggingMessageNotification platformSpecificHandler) ;


    /** Start Logging . By component manager */
    boolean stopRemoteLoggingService();

    /** Removing this method as currently sadl consumes all the zirk message. hence no registration*/
   // boolean initRemoteLogger(Comms comms, Device device);

    /** set logger to enable or disable **/
    boolean enableLogging(boolean enableRemoteLogging, String[] sphereName);

    /** check whether the remoteLogging is enabled or not */
    boolean isRemoteLoggingEnabled();

    /** send the control ledger or event ledger to logging */
    boolean sendRemoteLogLedgerMessage(Ledger ledger) ;

    /** send the control ledger to logging */
    //boolean sendRemoteLogMessage(ControlLedger tcMessage) ;

    /** send the control message to logging */
    boolean sendRemoteLogControlMessage(ControlMessage msg) ;

    boolean enableRemoteLoggingForAllSpheres();

    /** send the event message to logging */
   // boolean sendRemoteLogMessage(EventLedger eLedger) ;

    /** Not required this method  */
    //boolean isRemoteMessageValid(RemoteLoggingMessage logMessage);
}
