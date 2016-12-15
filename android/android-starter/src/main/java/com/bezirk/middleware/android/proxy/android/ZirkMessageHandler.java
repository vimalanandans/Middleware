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
package com.bezirk.middleware.android.proxy.android;

import android.content.Context;
import android.content.Intent;

import com.bezirk.middleware.core.actions.StreamAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.proxy.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZirkMessageHandler implements MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageHandler.class);

    private static final String FIRE_INTENT_ACTION = "com.bezirk.middleware.broadcast";
    private final Context applicationContext;

    public ZirkMessageHandler(Context context) {
        this.applicationContext = context;
    }

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        final Intent intent = new Intent();
        intent.putExtra("message", eventIncomingMessage);
        sendZirkIntent(intent);
    }

    @Override
    public void onIncomingStreamEvent(StreamAction streamMessage) {
        final Intent intent = new Intent();
        intent.putExtra("message", streamMessage);
        sendZirkIntent(intent);
    }

    private void sendZirkIntent(Intent intent) {
        intent.setAction(FIRE_INTENT_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (null != applicationContext) {
            applicationContext.sendBroadcast(intent);
        } else {
            logger.error("Cannot send Zirk intent because applicationContext is null");
        }
    }
}
