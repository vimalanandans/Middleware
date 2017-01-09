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
package com.bezirk.middleware.streaming;

import java.io.Serializable;

import com.bezirk.middleware.messages.StreamEvent;

/**
 *
 */

public class StreamReceiver {

    private StreamReceiver.StreamEventReceiver streamEventReceiver;

    public void setStreamEventReceiver(StreamEventReceiver streamEventReceiver) {
        this.streamEventReceiver = streamEventReceiver;
    }

    public StreamEventReceiver getStreamEventReceiver() {
        return streamEventReceiver;
    }

    /**
     * Interface implemented by the Zirk developers, Use this {@code streamEventReceiver} object
     * will be used to give a callback to  receiver.
     */
    public interface StreamEventReceiver extends Serializable {
        /**
         * Called to notify the subscriber that a new event was received.
         *
         * @param event  the received event
         */
        void receiveStreamEvent(StreamEvent event);

    }
}
