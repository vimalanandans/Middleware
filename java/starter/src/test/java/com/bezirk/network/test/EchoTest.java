//package com.bezirk.network.test;
//
//import com.bezirk.comms.BezirkComms;
//import com.bezirk.comms.BezirkCommsPC;
//import com.bezrik.network.UhuNetworkUtilities;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.MulticastSocket;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//import static org.junit.Assert.assertNotNull;
//
//public class EchoTest {
//	private static final Logger logger = LoggerFactory.getLogger(IpTest.class);
//
//	//Sockets for listeners
//	private MulticastSocket eMSocket;
//
//	//Listener Threads
//	private Thread eMListenerThread;
//
//	public static Boolean success = false;
//	@Before
//	public void setUpListener(){
//		BezirkCommsPC.init();
//
//		try {
//			eMSocket = new MulticastSocket(BezirkComms.getMULTICAST_PORT());
//
//			InetAddress addr = UhuNetworkUtilities.getIpForInterface(NetworkInterface.getByName(BezirkComms.getINTERFACE_NAME()));
//
//			assertNotNull("Could not compute Ip for NetworkInterface - Check Interface Name in comms.properties", addr);
//			eMSocket.setInterface(addr);
//		} catch (SocketException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		eMListenerThread = new Thread(new MulticastThread(eMSocket));
//		eMListenerThread.start();
//	}
//
//	@Test
//	public void test() {
//		this.sendMulticast(new String("Hello").getBytes());
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// commented by Vimal. This test is too specific to Bezirk / Udp Comms. hence this tests are not needed.
//		//assertTrue("Echo Test Failed.",EchoTest.success);
//	}
//
//	private boolean sendMulticast(byte[] sendData) {
//		InetAddress ipAddress;
//		DatagramPacket sendPacket;
//		try {
//			DatagramSocket clientSocket = new DatagramSocket();
//
//			ipAddress = InetAddress.getByName(BezirkComms.getMULTICAST_ADDRESS());
//			sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, BezirkComms.getMULTICAST_PORT());
//
//			clientSocket.send(sendPacket);
//			clientSocket.close();
//			logger.debug( "multicast Sent");
//			return true;
//
//		} catch (UnknownHostException e) {
//			logger.error( " Problem sending Muliticasts",e);
//			return false;
//		} catch (SocketException e) {
//			logger.error( " Problem sending Muliticasts",e);
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			logger.error(" Problem sending Muliticasts",e);
//			e.printStackTrace();
//			return false;
//		}
//
//	}
//
//	@After
//	public void killThread(){
//		eMSocket.close();
//		eMListenerThread.interrupt();
//	}
//
//}
