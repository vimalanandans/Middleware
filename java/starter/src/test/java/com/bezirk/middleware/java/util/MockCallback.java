package com.bezirk.middleware.java.util;

import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.proxy.MessageHandler;

/**
 * Mock callback zirk implementing BezirkCallback, used for unit testing
 */
public class MockCallback implements MessageHandler {

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        // TODO Auto-generated method stub

    }
}
