package com.bezirk.samples.protocols;

import com.bezirk.api.messages.ProtocolRole;

public class FileRequestProtocol extends ProtocolRole {
	
	private String[] eventTopics = {new FileRequest().topic};

	@Override
	public String getProtocolName() {
		return FileRequestProtocol.class.getSimpleName();
	}

	@Override
	public String getDescription() {
		return "Test protocol to request a file";
	}

	@Override
	public String[] getEventTopics() {
		return eventTopics==null ?null:eventTopics.clone();
	}

	@Override
	public String[] getStreamTopics() {
		return null;
	}

}
