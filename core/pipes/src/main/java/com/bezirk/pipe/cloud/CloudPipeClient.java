package com.bezirk.pipe.cloud;

import com.bezirk.control.messages.pipes.CloudResponse;
import com.bezirk.control.messages.pipes.CloudStreamResponse;
import com.bezirk.control.messages.pipes.PipeHeader;

public interface CloudPipeClient {

	/**
	 * Send a "general" serialized event and receive a serializedEvent response
	 * @param serializedEvent
	 * @return
	 */
	public CloudResponse sendEvent(PipeHeader pipeHeader, String serializedEvent); 
	
	/**
	 * Retrieves a multipart message with streamDescriptor + stream content
	 */
	public CloudStreamResponse retrieveContent(PipeHeader pipeHeader, String serializedEvent);
	
}
