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
    private static final Logger log = LoggerFactory.getLogger(ControlUnicastListener.class);

    private DatagramSocket ctrlUcastSocket;
    private Boolean running = false;
    private InetAddress myAddress;
    private ControlLedger receivedMessage;
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
        log.info("Control UnicastListener has Started");
        byte[] receiveData = new byte[UhuComms.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        running = true;
        myAddress = UhuNetworkUtilities.getLocalInet();

        while (running) {
            if (Thread.interrupted()) {
                log.info("Control UnicastListener has Stopped\n");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                ctrlUcastSocket.receive(receivePacket);
            } catch (SocketException e) {
                running = false;
                log.warn("UnicastListener has stopped \n", e);
                continue;
            } catch (IOException e) {
                log.warn("Datagram packet could not be received", e);
            }
            receivedMessage = new ControlLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!computedSender.equals(myAddress.getHostAddress())) {
                log.info("RECEIVED ON Control Unicast: " + receivePacket.getLength());
                //Set message is not local field
                receivedMessage.setIsMessageFromHost(false);
                //Construct Message -- with encMsg
                try {
                    if (ControlListenerUtility.constructMsg(receivedMessage, receivePacket, notification)) {
                        // Validate message
                        Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                        executor.execute(worker);
                    } else {
                        log.debug("duplicate/ msg received");
                    }
                } catch (Exception e) {
                    log.error("Error in decrypting the message", e);

                }
            } else {
                log.debug("[DISCARD]Control Unicast Received: " + receivePacket.getLength());
                continue;
            }


        }
    }


    public void stop() {
        running = false;
    }
}
