package com.bezirk.commstest.ui.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;

import com.bezirk.commstest.ui.CommsTestConstants;
import com.bezirk.commstest.ui.IUpdateResponse;
import com.bezirk.commstest.ui.PingMessage;
import com.bezirk.commstest.ui.PongMessage;
import com.bezrik.network.UhuNetworkUtilities;
import com.google.gson.Gson;

/**
 * Thread class that listens to a multicast port and updates the UI when a ping is reeived.
 *
 * @author AJC6KOR
 *
 */
public class MulticastReceiver extends Thread {
    
    private static final Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(MulticastReceiver.class);

    boolean isRunning;
    private MulticastSocket multicastSocket;
    
    private InetAddress myAddress;
    private final String deviceName;
    private final String deviceIP;

    private int multicastReceivingPort = CommsTestConstants.DEFAULT_MULTICAST_RECEIVING_PORT;
    
    private int unicastSendingPort = 2222;

    private final IUpdateResponse responseUI;
    
    public MulticastReceiver(String ctrlMCastAddr,IUpdateResponse responseUI,String deviceIP,String deviceName) {
        
        this.responseUI =responseUI;
        this.deviceIP = deviceIP;
        this.deviceName = deviceName; 
        
        try {
            multicastSocket = new MulticastSocket(multicastReceivingPort);
            multicastSocket.joinGroup(InetAddress.getByName(ctrlMCastAddr));
            isRunning = true;
            myAddress = UhuNetworkUtilities.getLocalInet();
            if (myAddress == null) {
                LOGGER.error("ERROR IN STARTING RECEIVER");
            }
        } catch (IOException e) {
            LOGGER.error("ERROR IN STARTING RECEIVER",e);
        }
    }

    @Override
    public void run() {
        super.run();
        final byte[] buf = new byte[1024];
        byte[] recData = new byte[1024];
        String yep;
        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

        while (isRunning) {
            if (Thread.interrupted()) {
                isRunning = false;
                continue;
            }
            LOGGER.debug("Started listening multcst");

            receivePacket.setData(buf);
            receivePacket.setLength(buf.length);
            try {
                multicastSocket.receive(receivePacket);
            } catch (Exception e) {
                isRunning = false;
                LOGGER.error("EXCEPTION IN RECEIVING",e);
                continue;
            }
            LOGGER.debug("Something received");

            if (myAddress
                    .getHostAddress()
                    .trim()
                    .equals(receivePacket.getAddress().getHostAddress()
                            .trim())) {
                LOGGER.debug("local ping received");
            } else {
                 recData= new byte[receivePacket.getLength()];
                System.arraycopy(receivePacket.getData(), 0, recData, 0,
                        receivePacket.getLength());
                 yep = new String(recData);
                if (isRunning) {
                    updatePingMessage(yep);
                }
            }
            receivePacket.setData(null);
            receivePacket.setLength(0);
        }
    }

    private void updatePingMessage(String yep) {
        try {
            final PingMessage msg = new Gson().fromJson(yep,
                    PingMessage.class);
            responseUI.updateUIPingReceived(msg);
            // send Pong
            sendPong(msg);
        } catch (Exception e) {
            LOGGER.error("Error in parsing JSON",e);
        }
    }

    /**
     * Stops the Thread
     */
    public void stopComms() {
        isRunning = false;
        if (multicastSocket != null) {
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
    
    private boolean sendPong(PingMessage msg) {
        // create a ongMessage
        final PongMessage pongMessage = new PongMessage();
        pongMessage.setSenderIP(deviceIP);
        pongMessage.setDeviceName(deviceName);
        final int pingId = msg.getPingId();
        pongMessage.setPingRequestId(msg.getDeviceName() + ":" + pingId);
        pongMessage.setPingId(pingId);

        final byte[] sendData = new Gson().toJson(pongMessage).getBytes();
        InetAddress ipAddress;
        try {
            final DatagramSocket clientSocket = new DatagramSocket();
            ipAddress = InetAddress.getByName(msg.getDeviceIp());
            DatagramPacket sendPacket;
            sendPacket = new DatagramPacket(sendData, sendData.length,
                    ipAddress, unicastSendingPort);
            clientSocket.send(sendPacket);
            clientSocket.close();
            responseUI.updateUIPongSent(msg);
            return true;
        } catch (Exception e) {
            LOGGER.error("ERROR in SENDING PONG",e);
        }
        return true;
    }

    /**
     * @param uSendingPort
     */
    public void updateConfiguration(int multicastReceivingPort,int uSendingPort) {

        this.multicastReceivingPort = multicastReceivingPort;
        this.unicastSendingPort =uSendingPort;
    }

}

