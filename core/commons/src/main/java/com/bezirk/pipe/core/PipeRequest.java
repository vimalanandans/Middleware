package com.bezirk.pipe.core;

import com.bezirk.api.IBezirkListener;
import com.bezirk.api.addressing.CloudPipe;
import com.bezirk.api.addressing.PipePolicy;
import com.bezirk.api.addressing.ServiceId;

public class PipeRequest {
	
	private ServiceId requestingService = null;
	
	// NOTE: we only support CloudPipes for now
	private CloudPipe pipe = null;
	private PipePolicy allowedIn = null;
	private PipePolicy allowedOut = null;
	private IBezirkListener listener = null;
	private String id;

	public PipeRequest(String id) {
		this.id = id;
	}

	public ServiceId getRequestingService() {
		return requestingService;
	}
	public void setRequestingService(ServiceId requestingService) {
		this.requestingService = requestingService;
	}
	public CloudPipe getPipe() {
		return pipe;
	}
	public void setPipe(CloudPipe requestedPipe) {
		this.pipe = requestedPipe;
	}
	public PipePolicy getAllowedIn() {
		return allowedIn;
	}
	public void setAllowedIn(PipePolicy allowedIn) {
		this.allowedIn = allowedIn;
	}
	public PipePolicy getAllowedOut() {
		return allowedOut;
	}
	public void setAllowedOut(PipePolicy allowedOut) {
		this.allowedOut = allowedOut;
	}
	public IBezirkListener getListener() {
		return listener;
	}
	public void setListener(IBezirkListener listener) {
		this.listener = listener;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
