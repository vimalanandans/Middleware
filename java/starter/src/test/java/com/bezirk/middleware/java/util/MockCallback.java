package com.bezirk.middleware.java.util;

import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.actions.ReceiveFileStreamAction;

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
}
