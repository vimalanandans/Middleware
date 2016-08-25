//package com.bezirk.middleware.java.network.test;
//
//import Comms;
//import com.bezirk.comms.BezirkCommsPC;
//import InterfaceInetPair;
//import com.bezrik.network.BezirkNetworkUtilities;
//
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.util.Enumeration;
//import java.util.Iterator;
//
//import static org.junit.Assert.fail;
//
//public class IpTest {
//	private static final Logger logger = LoggerFactory.getLogger(IpTest.class);
//
//	@Test
//	public void test1() {
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
//						logger.info("Interface: "+intf.getName()+" IPAddress: " + inetAddress.getHostAddress());
//					}
//				}
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//	}
//
//	@Test
//	public void test2() {
//		InetAddress addr;
//		try {
//			BezirkCommsPC.init();
//			addr = BezirkNetworkUtilities.getIpForInterface(NetworkInterface.getByName(Comms.getINTERFACE_NAME()));
//			if(addr == null){
//				logger.error("Failure to resolve ip - Check interface in comms.properties ");
//				logger.error("Possible interface/ ip pairs are: ");
//				Iterator<InterfaceInetPair> itr = BezirkNetworkUtilities.getInterfaceInetPair().iterator();
//				while(itr.hasNext()){
//					InterfaceInetPair pair = itr.next();
//					logger.error("Interface: "+pair.getNetworkInterface().getName()+" IP:"+pair.getInet().getHostAddress());
//				}
//				fail("Cannot resolve Ip");
//			}
//			logger.info("Address Obtained: "+ addr.getHostAddress());
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
//	}
//
//}
