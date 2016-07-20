package com.bezirk.remotelogging;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.devices.DeviceInterface;


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
    public static final String ALL_SPHERES = "ALL-SPHERES";

    /** Start Logging . By component manager */
    public boolean startLoggingService(final int loggingPort, final RemoteLoggingMessageNotification platformSpecificHandler) ;

    /** Start Logging . By component manager */
    public boolean stopLoggingService();

    /** initialize the remote logging module. */
    public boolean initRemoteLogger(Comms comms, DeviceInterface deviceInterface);

    /** set logger to enable or disable **/
    public boolean setLogger(boolean enable, String[] sphereName);

    /** is logging enabled. returns true if it is enabled */
    public boolean isEnabled();

    /** send the control ledger to logging */
    public boolean sendRemoteLogMessage(ControlLedger tcMessage) ;

    /** send the control message to logging */
    public boolean sendRemoteLogMessage(ControlMessage msg) ;

    /** send the event message to logging */
    public boolean sendRemoteLogMessage(EventLedger eLedger) ;

    public boolean isRemoteMessageValid(RemoteLoggingMessage logMessage);
}
