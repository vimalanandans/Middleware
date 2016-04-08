package com.bosch.upa.uhu.messagehandler;

import com.bezirk.api.addressing.Pipe;
import com.bosch.upa.uhu.pipe.policy.ext.UhuPipePolicy;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

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
