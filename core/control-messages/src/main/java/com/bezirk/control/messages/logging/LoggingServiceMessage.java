package com.bezirk.control.messages.logging;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

public class LoggingServiceMessage extends MulticastControlMessage {
	private final static Discriminator discriminator = ControlMessage.Discriminator.LoggingServiceMessage;
	
	protected String remoteLoggingServiceIP;
	protected int remoteLoggingServicePort;
	protected String[] sphereList;
	protected boolean loggingStatus;
	
	public LoggingServiceMessage() {}
	
	public LoggingServiceMessage(UhuServiceEndPoint sender, String sphereId, String serverIp, int serverPort,
			String[] sphereList, boolean loggingStatus) {
		super(sender, sphereId, discriminator);
		this.remoteLoggingServiceIP = serverIp;
		this.remoteLoggingServicePort = serverPort;
		this.sphereList = sphereList==null?null:sphereList.clone();
		this.loggingStatus = loggingStatus;
	}

	public String getRemoteLoggingServiceIP() {
		return remoteLoggingServiceIP;
	}

	public void setRemoteLoggingServiceIP(String remoteLoggingServiceIP) {
		this.remoteLoggingServiceIP = remoteLoggingServiceIP;
	}

	public int getRemoteLoggingServicePort() {
		return remoteLoggingServicePort;
	}

	public void setRemoteLoggingServicePort(int remoteLoggingServicePort) {
		this.remoteLoggingServicePort = remoteLoggingServicePort;
	}

	public String[] getSphereList() {
		return sphereList==null ?null:sphereList.clone();
	}

	public void setSphereList(String[] sphereList) {
		this.sphereList = sphereList==null ?null:sphereList.clone();
	}

	public boolean isLoggingStatus() {
		return loggingStatus;
	}

	public void setLoggingStatus(boolean loggingStatus) {
		this.loggingStatus = loggingStatus;
	}

}
