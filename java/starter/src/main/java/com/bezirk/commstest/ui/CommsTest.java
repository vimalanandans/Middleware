package com.bezirk.commstest.ui;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.commstest.ui.threads.UnicastReceiver;
import com.bezrik.network.UhuNetworkUtilities;
import com.google.gson.Gson;

/**
 * Class that handles the Ping test.
 */
public final class CommsTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommsTest.class);

    private final String name = UhuCompManager.getUpaDevice().getDeviceName();
    private int multicastSendingPort = CommsTestConstants.DEFAULT_MULTICAST_SENDING_PORT;
    private static final String CTRL_MULTICAST_ADDRESS = "224.5.5.5";
    private UnicastReceiver uReceiver;

    private final com.bezirk.commstest.ui.IUpdateResponse responseUI;
    private com.bezirk.commstest.ui.threads.MulticastReceiver mReceiver;
    private final UIStore uiStore = new UIStore();

    private final String myAddress = UhuNetworkUtilities.getDeviceIp();

    public CommsTest(com.bezirk.commstest.ui.IUpdateResponse response) {
        this.responseUI = response;
    }

    /**
     * Sends the ping
     * @param pingData
     */
    public void sendPing(int pingCount) {
        final PingMessage msg = new PingMessage();
        msg.pingId = pingCount;
        msg.deviceIp = myAddress;
        msg.deviceName = name;
        sendPingMsg(msg);

    }

    public void updateConfiguration(int uSendingPort, int uReceivingPort,
            int mSendingPort, int mReceivingPort) {
        multicastSendingPort = mSendingPort;
        mReceiver.updateConfiguration(mReceivingPort,uSendingPort);
        uReceiver.updateConfiguration(uReceivingPort);

    }

    /**
     * sends a ping
     * @param msg <DeviceName-PingId>
     */
    private void sendPingMsg(final PingMessage msg) {
        DatagramPacket sendPacket;
        try {
            final byte[] sendData = new Gson().toJson(msg).getBytes();
            final DatagramSocket clientSocket = new DatagramSocket();
            sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName(CTRL_MULTICAST_ADDRESS),
                    multicastSendingPort);
            clientSocket.send(sendPacket);
            clientSocket.close();
            responseUI.updateUIPingSent(msg);
            uiStore.addToWaitingPongList(msg.deviceName + ":" + msg.pingId);
        } catch (Exception e) {
            LOGGER.error("Exception in sending ping message.",e);
        }
    }

    /**
     * Starts the thread
     */
    public void startCommsReceiverThread() {
        if (null == mReceiver) {
            mReceiver = new com.bezirk.commstest.ui.threads.MulticastReceiver(CTRL_MULTICAST_ADDRESS,responseUI,myAddress,name);
        }
        mReceiver.startComms();
        if (null == uReceiver) {
            uReceiver = new UnicastReceiver(responseUI, uiStore);
        }
        uReceiver.startComms();
    }

    /**
     * Stops the thread
     */
    public void stopCommsReceiverThread() {
        if (mReceiver != null) {
            mReceiver.stopComms();
            mReceiver = null;
        }

        if (uReceiver != null) {
            uReceiver.stopComms();
            uReceiver = null;
        }
    }

    public String getSelectedServices(String pingReqId) {
        final Set<com.bezirk.commstest.ui.PongMessage> pongs = uiStore.getPongMap(pingReqId);
        if (pongs == null || pongs.isEmpty()) {
            return null;
        }
        final StringBuilder builder = new StringBuilder(35);
        for (final com.bezirk.commstest.ui.PongMessage pong : pongs) {
            builder.append("Device-Name: " + pong.deviceName + " IP: "
                    + pong.senderIP);
            builder.append('\n');
        }
        return builder.toString();
    }

}
