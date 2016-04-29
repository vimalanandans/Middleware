package com.bezirk.util;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;

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
