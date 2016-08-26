package com.bezirk.remotelogging;

import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Logging Client that is used to send the logger message to the remote Logging Zirk. Client will
 * be activated(started)/ Deactivated (Stopped) / Updated after LoggingService Message
 * is received from the Logging Zirk.
 */
public class RemoteLoggingClient {
    /**
     * private logger for the class
     */
    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingClient.class);
    /**
     * Remote Logging Zirk IP
     */
    private String serviceIP = null;
    /**
     * Remote Logging Zirk Port
     */
    private int servicePort = -1;
    /**
     * Processor for LogSenderQueue
     */
    private SenderQueueProcessor senderQueueProcessor = null;

    private final Date currentDate = new Date();

    RemoteLoggingClient()
    {

    }

    /** cehck the client is already running */
    public boolean isRunning()
    {
        if(senderQueueProcessor != null)
            return true;
        return false;
    }
    /**
     * Starts the client and the logger sender Processor.
     *
     * @param remoteIP - IP of the logging Zirk
     * @param port     - Port at which the logging Zirk is listening
     */
    public boolean  startClient(String remoteIP, int port) throws Exception {
        this.serviceIP = remoteIP;
        this.servicePort = port;
        senderQueueProcessor = new SenderQueueProcessor(this.serviceIP, this.servicePort);
        return senderQueueProcessor.startProcessing();
    }

    /**
     * Shuts the logging Client.
     *
     * @param remoteIP Ip of the logging zirk that is shutting
     * @param port     port at which the logging zirk was listening for the clients
     */
    public void stopClient(String remoteIP, int port) throws Exception {
        if (null != senderQueueProcessor &&
                remoteIP.equals(this.serviceIP) &&
                port == this.servicePort) {

            senderQueueProcessor.stopProcessing();
            senderQueueProcessor = null;
            serviceIP = null;
            servicePort = -1;

            return;
        }
        logger.info("unable to stop. may be already closed");
    }

    /**
     * Updates the Logging Client with new Logging Zirk Properties.
     *
     * @param newIP IP address of the new Logging Zirk
     * @param port  Port at which the Logging Zirk is listening
     */
    public boolean updateClient(String newIP, int port) throws Exception {

        if (!this.serviceIP.equals(newIP) || this.servicePort != port) {

            stopClient(this.serviceIP, this.servicePort);
            return startClient(newIP, port);
        }
        logger.debug("Received same LoggingService request to update the client");
        return true;
    }

    /** to send the incoming control message for logging */
    public boolean processLogInMessage(ControlMessage message) {

        boolean returnValue = false;

        if (Util.checkSphere(message.getSphereId()) ) {

            RemoteLoggingMessage remoteLoggingMessage = new RemoteLoggingMessage(
                    message.getSphereId(),
                    String.valueOf(currentDate.getTime()),
                    Util.CONTROL_RECEIVER_VALUE,
                    message.getUniqueKey(),
                    Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                    Util.LOGGING_VERSION);
            try {
                senderQueueProcessor.processLogOutMessage(remoteLoggingMessage.serialize());

                returnValue = true;
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }

        return returnValue;
    }


    /** to send the incoming event message for logging */
    public boolean processLogInMessage(Ledger ledger) {
        boolean returnValue = false;
        RemoteLoggingMessage remoteLoggingMessage = null;

        if(ledger instanceof EventLedger){
            if((null!=((EventLedger) ledger).getHeader())){
                logger.debug("Header is set "+((EventLedger) ledger).getHeader().toString());
                if(null!=((EventLedger) ledger).getHeader().getSphereId()){
                    logger.debug("sphere id is "+((EventLedger) ledger).getHeader().getSphereId());
                }
            }

            remoteLoggingMessage = new RemoteLoggingMessage(((EventLedger) ledger).getHeader().getSphereId(),
                    String.valueOf(currentDate.getTime()),
                    Util.CONTROL_RECEIVER_VALUE, ((EventLedger) ledger).getHeader().getUniqueMsgId(),
                    Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION);

            try {

                senderQueueProcessor.processLogOutMessage(remoteLoggingMessage.serialize());

                returnValue = true;

            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }else if(ledger instanceof ControlLedger){
            if(Util.checkSphere(((ControlLedger) ledger).getSphereId()))
            {
                remoteLoggingMessage = new RemoteLoggingMessage(
                        ((ControlLedger) ledger).getSphereId(),
                        String.valueOf(currentDate.getTime()),
                        Util.CONTROL_RECEIVER_VALUE,
                        ((ControlLedger) ledger).getMessage().getUniqueKey(),
                        Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                        Util.LOGGING_VERSION);
                try {
                    senderQueueProcessor.processLogOutMessage(remoteLoggingMessage.serialize());
                    returnValue = true;
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return returnValue;
    }

}
