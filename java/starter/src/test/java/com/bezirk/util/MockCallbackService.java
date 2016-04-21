package com.bezirk.util;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.messagehandler.ServiceMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;

/**
 * Mock callback service implementing IUhuCallback, used for unit testing
 *
 * @author AJC6KOR
 */
public class MockCallbackService implements ServiceMessageHandler {

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
