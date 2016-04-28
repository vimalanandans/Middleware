package com.bezirk.comms.udp.listeners;

import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.comms.BezirkComms;
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
    private static final Logger logger = LoggerFactory.getLogger(EventMulticastListener.class);

    private final MulticastSocket multicastSocket;
    private final ExecutorService executor;
    private Boolean running = false;
    private InetAddress myAddress;
    private IUhuCommsLegacy uhuComms = null;
    private CommsNotification commsErrNotificationError = null;


    public EventMulticastListener(MulticastSocket multicastSocket, IUhuCommsLegacy uhuComms, CommsNotification commsNotificationCallback) {
        this.multicastSocket = multicastSocket;
        this.commsErrNotificationError = commsNotificationCallback;
        executor = Executors.newFixedThreadPool(BezirkComms.getPOOL_SIZE());
        this.uhuComms = uhuComms;
    }

    @Override
    public void run() {
        byte[] buf = new byte[BezirkComms.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        try {
            multicastSocket.joinGroup(InetAddress.getByName(BezirkComms.getMULTICAST_ADDRESS()));
            myAddress = UhuNetworkUtilities.getLocalInet();
            if (myAddress == null) {
                logger.error("Cannot resolve Ip: About to stop thread");
                return;
            }
            running = true;
            logger.info("Event MulicastListener has Started\n");
        } catch (SocketException e1) {
            logger.warn("Error setting up Evt MulticastListener", e1);
        } catch (UnknownHostException e) {
            logger.warn("Error setting up Evt MulticastListener", e);
        } catch (IOException e) {
            logger.warn("Error setting up Evt MulticastListener", e);
        }

        while (running) {
            if (Thread.interrupted()) {
                logger.info("Event MulicastListener has Stopped");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(buf, buf.length);
            try {
                multicastSocket.receive(receivePacket);
            } catch (SocketTimeoutException e) {
                logger.warn("Event MulicastListener has Stopped \n", e);
            } catch (SocketException e) {
                logger.warn("Event MulicastListener has Stopped \n", e);
                running = false;
                continue;
            } catch (IOException e) {
                logger.warn("Event MulicastListener has Stopped \n", e);
            }
            EventLedger receivedMessage = new EventLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!computedSender.equals(myAddress.getHostAddress())) {
                logger.info("RECEIVED ON Multicast ");
                if (EventListenerUtility.constructMsg(receivedMessage, receivePacket, commsErrNotificationError)) {
                    //Validate the message
                    Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                    executor.execute(worker);
                }
            } else {
                //String retPayload = (String) UhuMessage.fromJson(received.split(",")[2].getBytes());
                logger.info("[DISCARD]Multicast Received: ");//+ " payload " + retPayload);
            }
        }
    }


    public void stop() {
        running = false;
    }
}
