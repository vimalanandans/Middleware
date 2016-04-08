package com.bosch.upa.uhu.pipe.core;

import com.bosch.upa.uhu.control.messages.pipes.PipeHeader;

/**
 * Class to encapsulate data needed to send the content result of a pipe
 * request to a local service
 */
public class LocalStreamSendJob {
	
	protected String streamDescriptor;
	
	protected String filePath;
	
	protected PipeHeader pipeHeader;

	public void setStreamDescriptor(String streamDescriptor) {
		this.streamDescriptor = streamDescriptor;
	}

	public String getStreamDescriptor() {
		return streamDescriptor;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public PipeHeader getPipeHeader() {
		return pipeHeader;
	}

	public void setPipeHeader(PipeHeader pipeHeader) {
		this.pipeHeader = pipeHeader;
	}

}
