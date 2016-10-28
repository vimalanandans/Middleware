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
package com.bezirk.middleware.java;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.util.Timer;
import java.util.TimerTask;

public class Publisher {

    private static final String PUBLISHER_ID = Main.getHostName() + ":Java:Sender";

    public Publisher() {
        final Bezirk bezirk = BezirkMiddleware.registerZirk(PUBLISHER_ID);

        if (bezirk == null) throw new AssertionError("BezirkMiddleware.registerZirk returned null");

        RequestReplySet requestReplySet = new RequestReplySet();
        requestReplySet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof Reply) {
                    Reply reply = (Reply) event;
                    System.out.println(reply.printMsgForReceiver());
                }
            }
        });

        bezirk.subscribe(requestReplySet);

        //publish messages periodically
        new Timer().scheduleAtFixedRate(new TimerTask() {
            private int messageNumber = 1;

            @Override
            public void run() {
                Request request = new Request(PUBLISHER_ID, messageNumber++);
                bezirk.sendEvent(request);
                System.out.println(request.printMsgForSender());
            }
        }, 2000, 1000);
    }

}
