package com.bezirk.util;

import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.proxy.messagehandler.ZirkMessageHandler;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;

/**
 * Mock callback zirk implementing BezirkCallback, used for unit testing
 *
 * @author AJC6KOR
 */
public class MockCallbackZirk implements ZirkMessageHandler {

    @Override
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStreamStatus(StreamStatusMessage streamStatusMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDiscoveryIncomingMessage(
            DiscoveryIncomingMessage discoveryCallback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {
        // TODO Auto-generated method stub

    }

}
