package com.bosch.upa.uhu.network;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;


/**
 * Helper class containing functions for networking, e.g. getting local IP and MAC address
 * @author Jan Zibuschka (jan.zibuschka@de.bosch.com) *
 */
public final class UhuNetworkUtilities {
	private static final Logger log = LoggerFactory.getLogger(UhuNetworkUtilities.class);
	private static NetworkInterface curInterface = null;

	private UhuNetworkUtilities(){
		//This is a utility class
	}
	public static List<IntfInetPair> getIntfInetPair(){
		ArrayList<IntfInetPair> list = new ArrayList<IntfInetPair>();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() /*&& inetAddress.isSiteLocalAddress()*/) {
						list.add(new IntfInetPair(intf, inetAddress));
					}

				}
			}			
		} catch (Exception ex) {
			log.error("Error occured while getting IntfInetPair \n", ex);
		}
		return list;
	}

	public static InetAddress getIpForInterface(NetworkInterface intf){
		if(intf == null){
			log.debug("Input interface is null");
			return null;
		}
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface curIntf = en.nextElement();

				if(intf.getDisplayName().equals(curIntf.getDisplayName())){
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
							log.debug("IP Address determined: " + inetAddress.getHostAddress());
							curInterface = intf;
							return inetAddress;
						}
					}
				}
				else{
					log.debug("Interface does not match");
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while getting IpForInterface \n", ex);
		}
		return null;
	}

	
	public static InetAddress getLocalInet(){
		if(curInterface == null){
			log.error("Input interface is null");
			return null;
		}
		for (Enumeration<InetAddress> enumIpAddr = curInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
			InetAddress inetAddress = enumIpAddr.nextElement();
			if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
				//log.info("IP Address determined: " + inetAddress.getHostAddress());
				return inetAddress;
			}
		}
		return null;
	}


	/**
	 * Gets a MAC address of a local network interface that is not a loopback interface
	 * @return Local MAC address
	 */
	public static byte[] getLocalMACAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
						//                        Log.i(TAG,"MAC Address determined: " + Hex.encodeToString(intf.getHardwareAddress()));
						return intf.getHardwareAddress();
					}

				}
			}
		} catch (Exception ex) {
			log.error("Error occured while getting LocalMACAddress \n", ex);
		}
		return null;
	}

	public static UhuServiceEndPoint getServiceEndPoint(UhuServiceId serviceId){
		UhuServiceEndPoint sep = new UhuServiceEndPoint(serviceId);
		sep.device = getLocalInet().getHostAddress();
		return  sep;
	}  
	
	/**
	 * get the device ip address detail
	 * */
	public static String getDeviceIp(){		
		return getLocalInet().getHostAddress();		
	} 

}
