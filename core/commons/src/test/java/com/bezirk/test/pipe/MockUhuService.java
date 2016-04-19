package com.bezirk.test.pipe;

import java.io.InputStream;
import java.util.Set;

import com.bezirk.middleware.IBezirkListener;
import com.bezirk.middleware.addressing.DiscoveredService;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.proxy.registration.ServiceRegistration;

public class MockUhuService implements IBezirkListener {

	private UhuServiceId serviceId = new UhuServiceId(ServiceRegistration.generateUniqueServiceID());
	
	private boolean pipeGrantedCalled = false;
	private boolean pipeGranted = false;
	
	public ServiceId getServiceId() {
		return serviceId;
	}

	@Override
	public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveStream(String topic, String stream, short streamId,
			InputStream f, ServiceEndPoint sender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveStream(String topic, String stream, short streamId,
			String filePath, ServiceEndPoint sender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void streamStatus(short streamId, StreamConditions status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pipeGranted(Pipe p, PipePolicy allowedIn, PipePolicy allowedOut) {
		pipeGrantedCalled = true;
		pipeGranted = true;
	}

	@Override
	public void pipeStatus(Pipe p, PipeConditions status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discovered(Set<DiscoveredService> serviceSet) {
		// TODO Auto-generated method stub

	}
	
	/*
	 * Getters and setters
	 */
	public boolean isPipeGrantedCalled() {
		return pipeGrantedCalled;
	}

	public void setPipeGrantedCalled(boolean pipeGrantedCalled) {
		this.pipeGrantedCalled = pipeGrantedCalled;
	}

	public boolean isPipeGranted() {
		return pipeGranted;
	}

	public void setPipeGranted(boolean pipeGranted) {
		this.pipeGranted = pipeGranted;
	}

}
