package com.bosch.upa.uhu.pipe.core;

import com.bosch.upa.uhu.control.messages.pipes.PipeHeader;

/**
 *  This interface represents the connection point from the platform-independent pipes component
 *  to each platform.  Specifically, each platform should implement this interface in order to have
 *  messages routed from remote systems connected by pipes to a locally running uhu instance
 */
public interface LocalUhuSender {
	
	/**
	 * Invoke the appropriate uhu services' receive method for the specified event
	 * @param header Specifies the message type (unicast or multicast) and addressing constraints
	 * @param serializedEvent The serialized event to be delivered to interested services
	 */
	void invokeReceive(PipeHeader pipeHeader, String serializedEvent);
	
	/**
	 * Invoke the appropriate uhu services' incoming() method so that streams
	 * can be delivered to the local uhu services
	 * @param header Specifies the message type (unicast or multicast) and addressing constraints
	 * @param serializedStream Stream descriptor specifying details about the stream content
	 * @param path Local path where the streamed content can be retrieved
	 */
	void invokeIncoming(PipeHeader pipeHeader, String serializedStream, String path);
}
