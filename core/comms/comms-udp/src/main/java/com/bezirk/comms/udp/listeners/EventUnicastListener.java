package com.bezirk.comms.udp.listeners;

import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.comms.UhuComms;
import com.bezirk.comms.udp.validation.MessageValidators;
import com.bezirk.control.messages.EventLedger;
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

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *         The UhuCommsUnicastListener is a thread that listens for messages that unicasted on the Uhu network
 *         On receiving a unicastPacket, the UhuCommsMulticastListener recreates the PackagedMessage and populates the ReceiverMessageQueue
 *         Note: the UhuCommsUnicastListener drops all echo messages(messages sent by the host device)
 */
public class EventUnicastListener implements Runnable {
    private final DatagramSocket unicastSocket;
    private final ExecutorService executor;
    private Logger log = LoggerFactory.getLogger(EventUnicastListener.class);
    private Boolean running = false;
    private InetAddress myAddress;
    private EventLedger receivedMessage;
    private IUhuCommsLegacy uhuComms = null;
    private ICommsNotification commsErrNotificationError = null;


    public EventUnicastListener(DatagramSocket unicastSocket, IUhuCommsLegacy uhuComms, ICommsNotification commsNotificationCallback) {
        this.unicastSocket = unicastSocket;
        this.commsErrNotificationError = commsNotificationCallback;
        executor = Executors.newFixedThreadPool(UhuComms.getPOOL_SIZE());
        this.uhuComms = uhuComms;
    }

    @Override
    public void run() {
        byte[] receiveData = new byte[UhuComms.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        running = true;
        myAddress = UhuNetworkUtilities.getLocalInet();
        if (myAddress == null) {
            log.error("Cannot resolve Ip: About to stop thread");
            return;
        }
        log.info("Event UnicastListener has Started");
        while (running) {
            if (Thread.interrupted()) {
                log.info("Event UnicastListener has Stopped\n");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                unicastSocket.receive(receivePacket);
            } catch (SocketException e) {
                running = false;
                log.warn("UnicastListener has stopped \n", e);
                continue;
            } catch (IOException e) {
                log.warn("UnicastListener has stopped \n", e);
            }
            receivedMessage = new EventLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!receivePacket.getAddress().getHostAddress().trim().equals(myAddress.getHostAddress().trim())) {
                log.info("RECEIVED ON Event Unicast: ");
                if (EventListenerUtility.constructMsg(receivedMessage, receivePacket, commsErrNotificationError)) {
                    //Validate the message
                    Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                    executor.execute(worker);
                }
            } else {
                //String retPayload = (String) UhuMessage.fromJSON(received.split(",")[2].getBytes());
                log.info("[DISCARD]Unicast Received: ");//+ " payload " + retPayload);
                continue;
            }

        }
    }

    public void stop() {
        running = false;
    }
}