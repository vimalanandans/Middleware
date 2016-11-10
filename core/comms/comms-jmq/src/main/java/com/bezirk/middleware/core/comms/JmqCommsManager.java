/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.CommsProcessor;
import com.bezirk.middleware.core.comms.processor.WireMessage;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.UUID;

public class JmqCommsManager extends CommsProcessor implements Receiver.OnMessageReceivedListener {
    private static final Logger logger = LoggerFactory.getLogger(JmqCommsManager.class);
    private final Peer comms;

    /**
     * @param groupName - Name to channel your application
     */
    public JmqCommsManager(@NotNull final String groupName, final CommsNotification commsNotification) {
        super(commsNotification);
        if (groupName == null) {
            throw new IllegalArgumentException("Group name passed as null when initializing JmqCommsManager");
        }
        comms = new Peer(groupName, this);
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        if (comms != null) {
            comms.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        if (comms != null) {
            comms.send(UUID.fromString(nodeId), msg);
            return true;
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        LifeCycleObservable lifeCycleObservable = (LifeCycleObservable) observable;
        switch (lifeCycleObservable.getState()) {
            case RUNNING:
                if (comms != null) {
                    logger.debug("Starting comms");
                    comms.start();
                    super.startComms();
                }
                break;
            case STOPPED:
                if (comms != null) {
                    logger.debug("Stopping comms");
                    comms.stop();
                    super.stopComms();
                }
        }
    }

    @Override
    public boolean processIncomingMessage(String nodeId, byte[] data) {
        try {
            processWireMessage(nodeId, new String(data, WireMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }
        return true;
    }

    @Override
    public String getNodeId() {
        return (comms != null) ? (comms.getId() != null) ? comms.getId().toString() : null : null;
    }
}

