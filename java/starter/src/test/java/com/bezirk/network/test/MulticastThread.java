//package com.bezirk.network.test;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
//import java.net.MulticastSocket;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.net.SocketTimeoutException;
//import java.net.UnknownHostException;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bezirk.comms.BezirkComms;
//import com.bezrik.network.UhuNetworkUtilities;
//
///**
// * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
// * The UhuCommsMulticastListener is a thread that listens for messages that multicasted on the Uhu network
// * On receiving a multicastPacket, the UhuCommsMulticastListener recreates the PackagedMessage and populates the ReceiverMessageQueue
// * Note: UhuCommsMulticastListener will drop all echo messages(messages that are sent by the host device).
// */
//public class MulticastThread implements Runnable {
//    private static final Logger logger = LoggerFactory
//            .getLogger(MulticastThread.class);
//
//    private final MulticastSocket multicastSocket;
//    private Boolean running = false;
//
//    public MulticastThread(MulticastSocket multicastSocket) {
//        this.multicastSocket = multicastSocket;
//    }
//
//    @Override
//    public void run() {
//
//        byte[] buf = new byte[BezirkComms.getMAX_BUFFER_SIZE()];
//        DatagramPacket receivePacket;
//        InetAddress myAddress = setUpListener();
//
//        while (running) {
//            if (Thread.interrupted()) {
//                logger.info("Event MulicastListener has Stopped");
//                running = false;
//                continue;
//            }
//            receivePacket = new DatagramPacket(buf, buf.length);
//            try {
//                multicastSocket.receive(receivePacket);
//            } catch (SocketTimeoutException e) {
//                logger.error("Unable to receive data through multicast socket . ",e);
//            } catch (SocketException e) {
//                logger.info("Event MulicastListener has Stopped \n");
//                running = false;
//                continue;
//            } catch (IOException e) {
//                logger.error("Unable to receive data through multicast socket . ",e);
//            }
//
//            if (!receivePacket.getAddress().getHostAddress()
//                    .equals(myAddress.getHostAddress())) {
//                logger.info("Echo Received: SUCCESS");
//                EchoTest.success = true;
//                continue;
//
//
//            } else {
//                logger.error("RECEIVED ON Multicast: "
//                        + "Problem Computing my Address");
//
//            }
//
//        }
//    }
//
//    private InetAddress setUpListener() {
//        InetAddress myAddress =null;
//
//        try {
//            multicastSocket.joinGroup(InetAddress.getByName(BezirkComms
//                    .getMULTICAST_ADDRESS()));
//            myAddress = UhuNetworkUtilities.getIpForInterface(NetworkInterface
//                    .getByName(BezirkComms.getINTERFACE_NAME()));
//            running = true;
//            logger.info("Event MulicastListener has Started\n");
//
//
//        } catch (SocketException e) {
//            logger.error("Unable to setup multicast socket . ",e);
//        } catch (UnknownHostException e) {
//            logger.error("Unable to setup multicast socket . ",e);
//        } catch (IOException e) {
//            logger.error("Unable to setup multicast socket . ",e);
//        }
//
//        return myAddress;
//    }
//
//    public void stop() {
//        running = false;
//    }
//}
