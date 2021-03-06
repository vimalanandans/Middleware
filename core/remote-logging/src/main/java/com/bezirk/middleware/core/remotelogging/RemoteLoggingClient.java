/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.remotelogging;

import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.Ledger;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

/**
 * Logging Client that is used to send the logger message to the remote Logging Zirk. Client will
 * be activated(started)/ Deactivated (Stopped) / Updated after LoggingService Message
 * is received from the Logging Zirk.
 */
public class RemoteLoggingClient {
    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingClient.class);
    private final Date currentDate = new Date();
    private final InetAddress inetAddress;
    /**
     * Remote Logging Zirk IP
     */
    private String serviceIP;
    /**
     * Remote Logging Zirk Port
     */
    private int servicePort = -1;
    /**
     * Processor for LogSenderQueue
     */
    private SenderQueueProcessor senderQueueProcessor;

    RemoteLoggingClient(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    /**
     * check the client is already running
     */
    public boolean isRunning() {
        return senderQueueProcessor != null;
    }

    /**
     * Starts the client and the logger sender Processor.
     *
     * @param remoteIP - IP of the logging Zirk
     * @param port     - Port at which the logging Zirk is listening
     */
    public boolean startClient(String remoteIP, int port) {
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
    public void stopClient(@NotNull String remoteIP, int port) throws IOException {
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
    public boolean updateClient(String newIP, int port) throws IOException {
        if (!this.serviceIP.equals(newIP) || this.servicePort != port) {
            stopClient(this.serviceIP, this.servicePort);
            return startClient(newIP, port);
        }

        logger.debug("Received same LoggingService request to update the client");
        return true;
    }

    /**
     * to send the incoming control message for logging
     */
    public boolean processLogInMessage(@NotNull ControlMessage message) {
        boolean returnValue = false;

        if (Util.checkSphere(message.getSphereId())) {
            final RemoteLoggingMessage remoteLoggingMessage = new RemoteLoggingMessage(
                    message.getSphereId(),
                    String.valueOf(currentDate.getTime()),
                    inetAddress.getHostAddress(),
                    Util.CONTROL_RECEIVER_VALUE,
                    message.getUniqueKey(),
                    Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                    Util.LOGGING_VERSION);

            try {
                senderQueueProcessor.processLogOutMessage(remoteLoggingMessage.serialize());

                returnValue = true;
            } catch (InterruptedException e) {
                logger.error("Processing of outgoing remote logging messages interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        return returnValue;
    }


    /**
     * to send the incoming event message for logging
     */
    public boolean processLogInMessage(Ledger ledger) {
        if (!isRunning()) {
            return false;
        }

        final RemoteLoggingMessage remoteLoggingMessage;

        if (ledger instanceof EventLedger) {
            final EventLedger eventLedger = (EventLedger) ledger;
            if (eventLedger.getHeader() != null) {
                logger.debug("Header is set {}", eventLedger.getHeader().toString());
                if (eventLedger.getHeader().getSphereId() != null) {
                    logger.debug("sphere id is {}", eventLedger.getHeader().getSphereId());
                }
            }

            remoteLoggingMessage = new RemoteLoggingMessage(((EventLedger) ledger).getHeader().getSphereId(),
                    String.valueOf(currentDate.getTime()), inetAddress.getHostAddress(),
                    Util.CONTROL_RECEIVER_VALUE, ((EventLedger) ledger).getHeader().getUniqueMsgId(),
                    Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION);
        } else if (ledger instanceof ControlLedger) {
            if (Util.checkSphere(((ControlLedger) ledger).getSphereId())) {
                remoteLoggingMessage = new RemoteLoggingMessage(
                        ((ControlLedger) ledger).getSphereId(),
                        String.valueOf(currentDate.getTime()),
                        inetAddress.getHostAddress(),
                        Util.CONTROL_RECEIVER_VALUE,
                        ((ControlLedger) ledger).getMessage().getUniqueKey(),
                        Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                        Util.LOGGING_VERSION);
            } else {
                remoteLoggingMessage = null;
            }
        } else {
            remoteLoggingMessage = null;
        }

        if (remoteLoggingMessage != null) {
            try {
                senderQueueProcessor.processLogOutMessage(remoteLoggingMessage.serialize());
                return true;
            } catch (InterruptedException e) {
                logger.error("Processing of outgoing remote logging messages interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        return false;
    }

}
