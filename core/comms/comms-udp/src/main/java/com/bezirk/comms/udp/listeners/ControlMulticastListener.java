package com.bezirk.comms.udp.listeners;

import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.comms.UhuComms;
import com.bezirk.comms.udp.validation.MessageValidators;
import com.bezirk.control.messages.ControlLedger;
import com.bezrik.network.UhuNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlMulticastListener implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ControlMulticastListener.class);

    private final MulticastSocket multicastSocket;
    private final ExecutorService executor;
    private Boolean running = false;
    private InetAddress myAddress;
    private IUhuCommsLegacy uhuComms = null;
    private ICommsNotification commsErrNotificationError = null;

    public ControlMulticastListener(MulticastSocket multicastSocket, IUhuCommsLegacy uhuComms,
                                    ICommsNotification commsNotificationCallback) {
        this.multicastSocket = multicastSocket;
        this.commsErrNotificationError = commsNotificationCallback;
        executor = Executors.newFixedThreadPool(UhuComms.getPOOL_SIZE());
        this.uhuComms = uhuComms;
    }

    @Override
    public void run() {
        byte[] buf = new byte[UhuComms.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        try {
            multicastSocket.joinGroup(InetAddress.getByName(UhuComms.getCTRL_MULTICAST_ADDRESS()));
            running = true;
            myAddress = UhuNetworkUtilities.getLocalInet();
            if (myAddress == null) {
                logger.error("Cannot resolve Ip: About to stop thread");
                return;
            }
            logger.info("Control MulicastListener has Started");

        } catch (IOException e) {
            logger.error("Error setting up Ctrl MulticastListener", e);
        }

        while (running) {
            if (Thread.interrupted()) {
                logger.info("Control MulicastListener has Stopped ");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(buf, buf.length);
            try {
                multicastSocket.receive(receivePacket);
            } catch (SocketTimeoutException e) {
                logger.error("Socket has timed out", e);
            } catch (SocketException e) {
                logger.error("Control MulicastListener has Stopped \n", e);
                running = false;
                continue;
            } catch (IOException e) {
                logger.error("IO Exception occured", e);
            }

            ControlLedger receivedMessage = new ControlLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!computedSender.equals(myAddress.getHostAddress())) {
                logger.info("RECEIVED ON Control Multicast: " + receivePacket.getLength());
                receivedMessage.setIsMessageFromHost(false);
                try {
                    if (ControlListenerUtility.constructMsg(receivedMessage, receivePacket, commsErrNotificationError)) {
                        Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                        executor.execute(worker);
                    } else {
                        logger.debug("Duplicate msg");
                    }
                } catch (Exception e) {
                    logger.error("Error in decrypting the message", e);
                }
            } else {
                //String retPayload = (String) UhuMessage.fromJson(received.split(",")[2].getBytes());
                logger.info("[DISCARD]Control Multicast Received: " + receivePacket.getLength());//+ " payload " + retPayload);
            }
        }
    }


    public void stop() {
        running = false;
    }
}