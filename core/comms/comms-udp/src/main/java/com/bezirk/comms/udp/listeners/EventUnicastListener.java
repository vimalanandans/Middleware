package com.bezirk.comms.udp.listeners;

import com.bezirk.comms.BezirkCommsLegacy;
import com.bezirk.comms.BezirkCommunications;
import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.udp.validation.MessageValidators;
import com.bezirk.control.messages.EventLedger;
import com.bezrik.network.BezirkNetworkUtilities;

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
 *         The UhuCommsUnicastListener is a thread that listens for messages that unicasted on the Bezirk network
 *         On receiving a unicastPacket, the UhuCommsMulticastListener recreates the PackagedMessage and populates the ReceiverMessageQueue
 *         Note: the UhuCommsUnicastListener drops all echo messages(messages sent by the host device)
 */
public class EventUnicastListener implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(EventUnicastListener.class);

    private final DatagramSocket unicastSocket;
    private final ExecutorService executor;
    private Boolean running = false;
    private BezirkCommsLegacy uhuComms = null;
    private CommsNotification commsErrNotificationError = null;


    public EventUnicastListener(DatagramSocket unicastSocket, BezirkCommsLegacy uhuComms, CommsNotification commsNotificationCallback) {
        this.unicastSocket = unicastSocket;
        this.commsErrNotificationError = commsNotificationCallback;
        executor = Executors.newFixedThreadPool(BezirkCommunications.getPOOL_SIZE());
        this.uhuComms = uhuComms;
    }

    @Override
    public void run() {
        byte[] receiveData = new byte[BezirkCommunications.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        running = true;
        InetAddress myAddress = BezirkNetworkUtilities.getLocalInet();
        if (myAddress == null) {
            logger.error("Cannot resolve Ip: About to stop thread");
            return;
        }
        logger.info("Event UnicastListener has Started");
        while (running) {
            if (Thread.interrupted()) {
                logger.info("Event UnicastListener has Stopped\n");
                running = false;
                continue;
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                unicastSocket.receive(receivePacket);
            } catch (SocketException e) {
                running = false;
                logger.warn("UnicastListener has stopped \n", e);
                continue;
            } catch (IOException e) {
                logger.warn("UnicastListener has stopped \n", e);
            }
            EventLedger receivedMessage = new EventLedger();
            String computedSender = receivePacket.getAddress().getHostAddress().trim();
            if (!receivePacket.getAddress().getHostAddress().trim().equals(myAddress.getHostAddress().trim())) {
                logger.info("RECEIVED ON Event Unicast: ");
                if (EventListenerUtility.constructMsg(receivedMessage, receivePacket, commsErrNotificationError)) {
                    //Validate the message
                    Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
                    executor.execute(worker);
                }
            } else {
                //String retPayload = (String) UhuMessage.fromJson(received.split(",")[2].getBytes());
                logger.info("[DISCARD]Unicast Received: ");//+ " payload " + retPayload);
            }
        }
    }

    public void stop() {
        running = false;
    }
}