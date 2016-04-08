package com.bosch.upa.uhu.network.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.comms.UhuComms;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * The UhuCommsMulticastListener is a thread that listens for messages that multicasted on the Uhu network
 * On receiving a multicastPacket, the UhuCommsMulticastListener recreates the PackagedMessage and populates the ReceiverMessageQueue
 * Note: UhuCommsMulticastListener will drop all echo messages(messages that are sent by the host device).
 */
public class MulticastThread implements Runnable {
    private static final Logger log = LoggerFactory
            .getLogger(MulticastThread.class);

    private final MulticastSocket multicastSocket;
    private Boolean running = false;

    public MulticastThread(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {

        byte[] buf = new byte[UhuComms.getMAX_BUFFER_SIZE()];
        DatagramPacket receivePacket;
        InetAddress myAddress = setUpListener();

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
                log.error("Unable to receive data through multicast socket . ",e);
            } catch (SocketException e) {
                log.info("Event MulicastListener has Stopped \n");
                running = false;
                continue;
            } catch (IOException e) {
                log.error("Unable to receive data through multicast socket . ",e);
            }

            if (!receivePacket.getAddress().getHostAddress()
                    .equals(myAddress.getHostAddress())) {
                log.info("Echo Received: SUCCESS");
                EchoTest.success = true;
                continue;
                

            } else {
                log.error("RECEIVED ON Multicast: "
                        + "Problem Computing my Address");
               
            }

        }
    }

    private InetAddress setUpListener() {
        InetAddress myAddress =null;
        
        try {
            multicastSocket.joinGroup(InetAddress.getByName(UhuComms
                    .getMULTICAST_ADDRESS()));
            myAddress = UhuNetworkUtilities.getIpForInterface(NetworkInterface
                    .getByName(UhuComms.getINTERFACE_NAME()));
            running = true;
            log.info("Event MulicastListener has Started\n");
            
            
        } catch (SocketException e) {
            log.error("Unable to setup multicast socket . ",e);
        } catch (UnknownHostException e) {
            log.error("Unable to setup multicast socket . ",e);
        } catch (IOException e) {
            log.error("Unable to setup multicast socket . ",e);
        }
        
        return myAddress;
    }

    public void stop() {
        running = false;
    }
}
