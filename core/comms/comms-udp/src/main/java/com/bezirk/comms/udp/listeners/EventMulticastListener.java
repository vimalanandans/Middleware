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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *         The UhuCommsMulticastListener is a thread that listens for messages that multicasted on the Uhu network
 *         On receiving a multicastPacket, the UhuCommsMulticastListener recreates the PackagedMessage and populates the ReceiverMessageQueue
 *         Note: UhuCommsMulticastListener will drop all echo messages(messages that are sent by the host device).
 */
public class EventMulticastListener implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(EventMulticastListener.class);

    private final MulticastSocket multicastSocket;
    private final ExecutorService executor;
    private Boolean running = false;
    private InetAddress myAddress;
    private EventLedger receivedMessage;
    private IUhuCommsLegacy uhuComms = null;
    private ICommsNotification commsErrNotificationError = null;


    public EventMulticastListener(MulticastSocket multicastSocket, IUhuCommsLegacy uhuComms, ICommsNotification commsNotificationCallback) {
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
            multicastSocket.joinGroup(InetAddress.getByName(UhuComms.getMULTICAST_ADDRESS()));
            myAddress = UhuNetworkUtilities.getLocalInet();
            if (myAddress == null) {
                log.error("Cannot resolve Ip: About to stop thread");
                return;
            }
            running = true;
            log.info("Event MulicastListener has Started\n");
        } catch (SocketException e1) {
            log.warn("Error setting up Evt MulticastListener", e1);
        } catch (UnknownHostException e) {
            log.warn("Error setting up Evt MulticastListener", e);
        } catch (IOException e) {
            log.warn("Error setting up Evt MulticastListener", e);
        }

        while (running) {
            if (Thread.interrupted()) {
                log.info("Event MulicastListener has Stopped");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(buf, buf.length);
            try {
                multicastSocket.receive(receivePacket);
            } catch (SocketTimeoutException e) {
                log.warn("Event MulicastListener has Stopped \n", e);
            } catch (SocketException e) {
                log.warn("Event MulicastListener has Stopped \n", e);
                running = false;
                continue;
            } catch (IOException e) {
                log.warn("Event MulicastListener has Stopped \n", e);
            }
            receivedMessage = new EventLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!computedSender.equals(myAddress.getHostAddress())) {
                log.info("RECEIVED ON Multicast ");
                if (EventListenerUtility.constructMsg(receivedMessage, receivePacket, commsErrNotificationError)) {
                    //Validate the message
                    Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                    executor.execute(worker);
                }
            } else {
                //String retPayload = (String) UhuMessage.fromJSON(received.split(",")[2].getBytes());
                log.info("[DISCARD]Multicast Received: ");//+ " payload " + retPayload);
                continue;
            }


        }
    }


    public void stop() {
        running = false;
    }
}
