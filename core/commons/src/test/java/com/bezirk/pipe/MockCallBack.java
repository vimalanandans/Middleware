package com.bezirk.pipe;

import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;

public class MockCallBack implements MessageHandler {
    private MockBezirkZirk mockBezirkZirk = null;

    public MockCallBack(MockBezirkZirk mockBezirkZirk) {
        super();
        this.mockBezirkZirk = mockBezirkZirk;
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

        this.mockBezirkZirk.setPipeGranted(true);
        this.mockBezirkZirk.setPipeGrantedCalled(true);


    }

}
