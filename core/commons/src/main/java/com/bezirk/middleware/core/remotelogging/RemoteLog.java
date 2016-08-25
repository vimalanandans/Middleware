package com.bezirk.middleware.core.remotelogging;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.device.Device;


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

    boolean startLoggingService(final RemoteLoggingMessageNotification platformSpecificHandler) ;

    /** Start Logging . By component manager */
    boolean stopLoggingService();

    /** initialize the remote logging module. */
    boolean initRemoteLogger(Comms comms, Device device);

    /** set logger to enable or disable **/
    boolean setLogger(boolean enable, String[] sphereName);

    /** is logging enabled. returns true if it is enabled */
    boolean isEnabled();

    /** send the control ledger to logging */
    boolean sendRemoteLogMessage(ControlLedger tcMessage) ;

    /** send the control message to logging */
    boolean sendRemoteLogMessage(ControlMessage msg) ;

    /** send the event message to logging */
    boolean sendRemoteLogMessage(EventLedger eLedger) ;

    boolean isRemoteMessageValid(com.bezirk.middleware.core.remotelogging.RemoteLoggingMessage logMessage);
}
