package com.bezirk.pipe;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;

public class MockCallBackZirk implements ZirkMessageHandler {

    private MockUhuZirk mockUhuservice = null;

    public MockCallBackZirk(MockUhuZirk mockUhuservice) {
        super();
        this.mockUhuservice = mockUhuservice;
    }

    @Override
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {

    }

    @Override
    public void onIncomingStream(
            StreamIncomingMessage streamIncomingMessage) {

    }


    @Override
    public void onStreamStatus(
            StreamStatusMessage streamStatusMessage) {

    }

    @Override
    public void onDiscoveryIncomingMessage(DiscoveryIncomingMessage discoveryCallback) {

    }

    @Override
    public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {

        this.mockUhuservice.setPipeGranted(true);
        this.mockUhuservice.setPipeGrantedCalled(true);


    }

}
