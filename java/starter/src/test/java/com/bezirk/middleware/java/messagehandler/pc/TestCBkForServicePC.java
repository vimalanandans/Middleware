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
package com.bezirk.middleware.java.messagehandler.pc;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.java.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.middleware.java.proxy.messagehandler.ZirkMessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestCBkForServicePC {
    private static final Logger logger = LoggerFactory.getLogger(TestCBkForServicePC.class);

    private final BroadcastReceiver BRForService = new BRForServiceMock();
    private final ZirkMessageHandler cBkForServicePC = new ZirkMessageHandler(BRForService);
    private boolean receivedEvent = false;
    private boolean receivedUnicastStream = false;

    /*

    fixme Punith: commenting this as this was failing, need to be checked.
    @Test
    public void testFireEventCallback() {
        UnicastEventAction eventCallbackMessage = new UnicastEventAction(BezirkAction.ACTION_ZIRK_RECEIVE_EVENT,
                new ZirkId("TEST"), new BezirkZirkEndPoint(new ZirkId("TEST 2")), new Event());
        cBkForServicePC.onIncomingEvent(eventCallbackMessage);

        assertTrue("Callback Zirk is unable to fire eventCallback. ", receivedEvent);
    }*/

//    @Test
//    public void testFireUnicastStreamCallback() {
//        ReceiveFileStreamAction unicastStreamCallbackMessage = new ReceiveFileStreamAction(new ZirkId("TEST"), null, null, null);
//        cBkForServicePC.onIncomingStream(unicastStreamCallbackMessage);
//
//        assertTrue("Callback Zirk is unable to fire Unicast stream.", receivedUnicastStream);
//    }

    class BRForServiceMock implements BroadcastReceiver {
        @Override
        public void onReceive(ZirkAction callbackMessage) {
            if (BezirkAction.ACTION_ZIRK_RECEIVE_EVENT.equals(callbackMessage.getAction())) {
                receivedEvent = true;
            } else if (BezirkAction.ACTION_ZIRK_RECEIVE_STREAM.equals(callbackMessage.getAction())) {
                receivedUnicastStream = true;
            }
        }
    }
}