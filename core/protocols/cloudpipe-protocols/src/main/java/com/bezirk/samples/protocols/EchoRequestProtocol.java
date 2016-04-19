package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.ProtocolRole;

public class EchoRequestProtocol extends ProtocolRole {
	
	private String protocolName = EchoRequestProtocol.class.getSimpleName();
	
	private String description = "Protocol containing echo request messages";
	
	private String[] eventTopics = {new EchoRequest().topic};
	
	private String[] streamTopics = {};

	@Override
	public String getProtocolName() {
		return protocolName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String[] getEventTopics() {
		return eventTopics==null ?null:eventTopics.clone();
	}

	@Override
	public String[] getStreamTopics() {
		return streamTopics==null ?null:streamTopics.clone();
	}

}
