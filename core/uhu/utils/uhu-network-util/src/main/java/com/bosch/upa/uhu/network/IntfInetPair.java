package com.bosch.upa.uhu.network;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class IntfInetPair {
	private final NetworkInterface intf;
	private final InetAddress inet;
	
	public IntfInetPair(NetworkInterface intf, InetAddress inet){
		this.intf = intf;
		this.inet = inet;
	}
	
	public NetworkInterface getIntf() {
		return intf;
	}
	
	public InetAddress getInet() {
		return inet;
	}
	
	
}
