package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

public class MulticastControlMessage extends ControlMessage {
	
	/**
     * Empty Constructor required for gson.deserialize
     */
	public MulticastControlMessage(){
	     // Empty Constructor required for gson.deserialize
	}

	/**
	 * Used if you want to send a custom key
	 * Generally only used with responses
	 * This constructor may not be used currently: leaving it here for later
	 * @param sender the sender-end-point
	 * @param sphereName This is the sphereId
	 * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
	 * @param key UniqueKey that is used to match responses to corresponding requests
	 */
	public MulticastControlMessage(UhuServiceEndPoint sender, String sphereId,
			Discriminator discriminator, String key){
		//Notice last boolean is set to true : This is because all multicasts are retransmitted
		super(sender, sphereId, discriminator, true, key);		
	}
	
	/**
	 * Used if you want the stack to auto-generate the key
	 * Generally only used with requests
	 * @param sender the sender-end-point
	 * @param sphereName This is the sphereId
	 * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
	 */
	public MulticastControlMessage(UhuServiceEndPoint sender, String sphereId,
			Discriminator discriminator){
		//Notice last boolean is set to true : This is because all multicasts are retransmitted
		super(sender, sphereId, discriminator, true);		
	}

}
