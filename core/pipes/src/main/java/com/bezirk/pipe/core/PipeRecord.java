package com.bezirk.pipe.core;

import com.bezirk.api.addressing.Pipe;
import com.bezirk.api.addressing.PipePolicy;

public class PipeRecord {
	
	private Pipe pipe = null;
	
	private PipePolicy allowedIn = null;
	
	private PipePolicy allowedOut = null;
	
	private String sphereId = null;
	
	private String password = null;
	
	private String certFile = null;

	public PipeRecord(Pipe pipe) {
		this.pipe = pipe;
	}
	
	public void setPipe(Pipe pipe) {
		this.pipe = pipe;
	}

	public Pipe getPipe() {
		return pipe;
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

	public String getSphereId() {
		return sphereId;
	}

	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCertFile() {
		return certFile;
	}

	public void setCertFile(String certFile) {
		this.certFile = certFile;
	}
}
