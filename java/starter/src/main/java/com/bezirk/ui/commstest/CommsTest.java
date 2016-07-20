package com.bezirk.ui.commstest;

import com.bezirk.ui.commstest.threads.UnicastReceiver;
import com.bezrik.network.BezirkNetworkUtilities;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

/**
 * Class that handles the Ping test.
 */
public final class CommsTest {
    private static final Logger logger = LoggerFactory.getLogger(CommsTest.class);

    private static final String CTRL_MULTICAST_ADDRESS = "224.5.5.5";
    private String deviceName ;
    private final IUpdateResponse responseUI;
    private final UIStore uiStore = new UIStore();
    private final String myAddress = BezirkNetworkUtilities.getDeviceIp();
    private int multicastSendingPort = CommsTestConstants.DEFAULT_MULTICAST_SENDING_PORT;
    private UnicastReceiver uReceiver;
    private com.bezirk.ui.commstest.threads.MulticastReceiver mReceiver;

    public CommsTest(IUpdateResponse response, String deviceName) {
        this.responseUI = response;
        this.deviceName = deviceName;
    }

    public void sendPing(int pingCount) {
        final PingMessage msg = new PingMessage();
        msg.pingId = pingCount;
        msg.deviceIp = myAddress;
        msg.deviceName = deviceName;
        sendPingMsg(msg);

    }

    public void updateConfiguration(int uSendingPort, int uReceivingPort,
                                    int mSendingPort, int mReceivingPort) {
        multicastSendingPort = mSendingPort;
        mReceiver.updateConfiguration(mReceivingPort, uSendingPort);
        uReceiver.updateConfiguration(uReceivingPort);

    }

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
            logger.error("Exception in sending ping message.", e);
        }
    }

    /**
     * Starts the thread
     */
    public void startCommsReceiverThread() {
        if (null == mReceiver) {
            mReceiver = new com.bezirk.ui.commstest.threads.MulticastReceiver(CTRL_MULTICAST_ADDRESS, responseUI, myAddress, deviceName);
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
        final Set<PongMessage> pongs = uiStore.getPongMap(pingReqId);
        if (pongs == null || pongs.isEmpty()) {
            return null;
        }
        final StringBuilder builder = new StringBuilder(35);
        for (final PongMessage pong : pongs) {
            builder.append("Device-Name: ");
            builder.append(pong.deviceName);
            builder.append(" IP: ");
            builder.append(pong.senderIP);
            builder.append('\n');
        }
        return builder.toString();
    }

}
