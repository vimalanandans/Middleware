package com.bezirk.util;

import com.bezirk.actions.UnicastEventAction;
import com.bezirk.proxy.MessageHandler;
import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.StreamStatusAction;

/**
 * Mock callback zirk implementing BezirkCallback, used for unit testing
 *
 * @author AJC6KOR
 */
public class MockCallback implements MessageHandler {

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onIncomingStream(ReceiveFileStreamAction receiveFileStreamAction) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStreamStatus(StreamStatusAction streamStatusAction) {
        // TODO Auto-generated method stub

    }
}
