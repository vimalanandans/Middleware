package com.bezirk.commstest.ui.threads;

import com.bezirk.comms.UhuComms;
import com.bezirk.commstest.ui.PongMessage;
import com.bezirk.commstest.ui.UIStore;
import com.bezirk.util.UhuValidatorUtility;
import com.bezrik.network.UhuNetworkUtilities;
import com.google.gson.Gson;

import org.slf4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Set;

/**
 * @author AJC6KOR
 */
public class UnicastReceiver extends Thread {

    private static final Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(UnicastReceiver.class);
    private final UIStore uiStore;
    private final com.bezirk.commstest.ui.IUpdateResponse responseUI;
    boolean isRunning;
    private DatagramSocket unicastListenerSocket;
    private int unicastReceivingPort = 2222;

    public UnicastReceiver(com.bezirk.commstest.ui.IUpdateResponse responseUI, UIStore uiStore) {

        this.uiStore = uiStore;
        this.responseUI = responseUI;

        NetworkInterface intf;
        InetAddress addr = null;
        try {
            intf = NetworkInterface.getByName(UhuComms.getINTERFACE_NAME());
            addr = UhuValidatorUtility.isObjectNotNull(intf) ? null
                    : UhuNetworkUtilities.getIpForInterface(intf);
            if (addr == null) {
                LOGGER.error("ERROR IN STARTING UNICAST LINSTNER");
            }
        } catch (SocketException e) {
            LOGGER.error("ERROR IN STARTING UNICAST LINSTNER", e);
        }
        try {
            unicastListenerSocket = new DatagramSocket(null/*unicastReceivingPort, addr*/);
            unicastListenerSocket.setReuseAddress(true);
            unicastListenerSocket.bind(new InetSocketAddress(addr,
                    unicastReceivingPort));
        } catch (SocketException e) {
            LOGGER.error("SOCKET ERROR", e);
        }
        isRunning = true;
    }

    @Override
    public void run() {
        super.run();
        final byte[] buf = new byte[1024];
        DatagramPacket receivePacket;

        while (isRunning) {
            if (Thread.interrupted()) {
                isRunning = false;
                continue;
            }
            LOGGER.debug("Started listening Unicst");
            receivePacket = new DatagramPacket(buf, buf.length);
            try {
                unicastListenerSocket.receive(receivePacket);
            } catch (Exception e) {
                isRunning = false;
                LOGGER.error("ERROR IN RECEVING UNICAST", e);
                continue;
            }
            final byte[] recData = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(), 0, recData, 0,
                    receivePacket.getLength());
            final String yep = new String(recData);
            LOGGER.debug("PING REPLY RECEIVED");
            if (isRunning) {
                updatePongMessage(yep);
            }
        }
    }

    private void updatePongMessage(String yep) {
        try {
            final PongMessage msg = new Gson().fromJson(yep, PongMessage.class);
            uiStore.updatePongStatus(msg.getPingRequestId(), msg);
            final Set<PongMessage> pongs = uiStore.getPongMap(msg
                    .getPingRequestId());
            if (pongs != null) {
                responseUI.updateUIPongReceived(msg, pongs.size());
            }
        } catch (Exception e) {
            LOGGER.error("Error in parsing JSON", e);
        }
    }

    /**
     * Stops the Thread
     */
    public void stopComms() {
        isRunning = false;
        if (unicastListenerSocket != null) {
            this.interrupt();
        }
    }

    /**
     * Starts the tread
     */
    public void startComms() {
        isRunning = true;
        this.start();
    }

    /**
     * @param uReceivingPort
     * @param uSendingPort
     */
    public void updateConfiguration(int uReceivingPort) {

        this.unicastReceivingPort = uReceivingPort;
    }

}