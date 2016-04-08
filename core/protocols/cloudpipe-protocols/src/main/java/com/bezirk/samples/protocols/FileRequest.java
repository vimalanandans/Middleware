package com.bezirk.samples.protocols;

import com.bezirk.api.messages.Event;
import com.bezirk.api.messages.GetStreamRequest;

public class FileRequest extends GetStreamRequest {
	
	public static final String SUB_TOPIC = FileRequest.class.getSimpleName();
	
	private String fileName = null;
	
	public FileRequest() {
		super(SUB_TOPIC);
	}
	
	public static FileRequest deserialize(String serializedEvent) {
		return Event.deserialize(serializedEvent, FileRequest.class);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
