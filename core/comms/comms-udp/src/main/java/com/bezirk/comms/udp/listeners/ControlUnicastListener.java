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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlUnicastListener implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ControlUnicastListener.class);

    private DatagramSocket ctrlUcastSocket;
    private Boolean running = false;
    private ExecutorService executor;
    private IUhuCommsLegacy uhuComms = null;
    private ICommsNotification notification = null;

    public ControlUnicastListener(DatagramSocket unicastSocket, IUhuCommsLegacy uhuComms, ICommsNotification notification) {
        this.ctrlUcastSocket = unicastSocket;
        this.notification = notification;
        executor = Executors.newFixedThreadPool(UhuComms.getPOOL_SIZE());
        this.uhuComms = uhuComms;
    }

    @Override
    public void run() {
        logger.info("Control UnicastListener has Started");
        byte[] receiveData = new byte[UhuComms.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        running = true;
        InetAddress myAddress = UhuNetworkUtilities.getLocalInet();

        while (running) {
            if (Thread.interrupted()) {
                logger.info("Control UnicastListener has Stopped\n");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                ctrlUcastSocket.receive(receivePacket);
            } catch (SocketException e) {
                running = false;
                logger.warn("UnicastListener has stopped \n", e);
                continue;
            } catch (IOException e) {
                logger.warn("Datagram packet could not be received", e);
            }
            ControlLedger receivedMessage = new ControlLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!computedSender.equals(myAddress.getHostAddress())) {
                logger.info("RECEIVED ON Control Unicast: " + receivePacket.getLength());
                //Set message is not local field
                receivedMessage.setIsMessageFromHost(false);
                //Construct Message -- with encMsg
                try {
                    if (ControlListenerUtility.constructMsg(receivedMessage, receivePacket, notification)) {
                        // Validate message
                        Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                        executor.execute(worker);
                    } else {
                        logger.debug("duplicate/ msg received");
                    }
                } catch (Exception e) {
                    logger.error("Error in decrypting the message", e);
                }
            } else {
                logger.debug("[DISCARD]Control Unicast Received: " + receivePacket.getLength());
            }
        }
    }


    public void stop() {
        running = false;
    }
}
