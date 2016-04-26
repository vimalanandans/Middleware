package com.bezirk.pipe;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.proxy.api.impl.UhuZirkId;
import com.bezirk.proxy.registration.ServiceRegistration;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

public class MockUhuService implements BezirkListener {

    private UhuZirkId serviceId = new UhuZirkId(ServiceRegistration.generateUniqueServiceID());

    private boolean pipeGrantedCalled = false;
    private boolean pipeGranted = false;

    public ZirkId getServiceId() {
        return serviceId;
    }

    @Override
    public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
        // TODO Auto-generated method stub

    }

    @Override
    public void receiveStream(String topic, String stream, short streamId,
                              InputStream inputStream, ZirkEndPoint sender) {
        // TODO Auto-generated method stub

    }

    @Override
    public void receiveStream(String topic, String stream, short streamId,
                              File file, ZirkEndPoint sender) {
        // TODO Auto-generated method stub

    }

    @Override
    public void streamStatus(short streamId, StreamStates status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pipeGranted(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut) {
        pipeGrantedCalled = true;
        pipeGranted = true;
    }

    @Override
    public void pipeStatus(Pipe pipe, PipeStates status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void discovered(Set<DiscoveredZirk> zirkSet) {
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
