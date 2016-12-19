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
package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.streaming.Stream;

/**
 * This class will have all the metadata infingormation of stream. which will be passed as intent
 * to the android receiver.
 */

public class StreamAction extends ZirkAction {

    private short streamId;
    private BezirkAction bezirkAction;
    private Stream stream;
    private String streamStatus;

    public StreamAction(ZirkId zirkId){
        super(zirkId);
    }

    public StreamAction(ZirkId zirkId, short streamId, Stream stream, BezirkAction bezirkAction){
        super(zirkId);
        this.stream  = stream;
        this.streamId = streamId;
        this.bezirkAction = bezirkAction;
    }

    @Override
    public BezirkAction getAction() {
        return bezirkAction;
    }

    public void setBezirkAction(BezirkAction bezirkAction) {
        this.bezirkAction = bezirkAction;
    }

    public Stream getStreamRequest() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public short getStreamId() {
        return streamId;
    }

    public void setStreamId(short streamId) {
        this.streamId = streamId;
    }

    public String getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(String streamStatus) {
        this.streamStatus = streamStatus;
    }

}
