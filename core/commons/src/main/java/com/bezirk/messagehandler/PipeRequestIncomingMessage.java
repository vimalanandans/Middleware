package com.bezirk.messagehandler;

import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.pipe.policy.ext.UhuPipePolicy;
import com.bezirk.proxy.api.impl.UhuServiceId;

public class PipeRequestIncomingMessage extends ServiceIncomingMessage {

	private Pipe pipe;
	private String pipeReqId;
	private UhuPipePolicy allowedIn;
	private UhuPipePolicy allowedOut;

	public PipeRequestIncomingMessage(){ // Empty Constructor for Gson
		this.callbackDiscriminator = "PIPE-APPROVED";		
	}
	
	public PipeRequestIncomingMessage(Pipe pipe, String pipeReqId, UhuPipePolicy allowedIn, UhuPipePolicy allowedOut, UhuServiceId recipient){
		this.callbackDiscriminator = "PIPE-APPROVED";		
		this.pipe = pipe;
		this.pipeReqId = pipeReqId;
		this.allowedIn = allowedIn;
		this.allowedOut = allowedOut;
		this.recipient = recipient;
	}


	public Pipe getPipe() {
		return pipe;
	}

	public String getPipeReqId() {
		return pipeReqId;
	}

	public UhuPipePolicy getAllowedIn() {
		return allowedIn;
	}

	public UhuPipePolicy getAllowedOut() {
		return allowedOut;
	}
}
