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
package com.bezirk.streaming;

import com.bezirk.middleware.core.streaming.StreamResponse;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * This will be Response type sent by the Reciver with updated information of StreamRecord.
 *
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamResponse extends StreamResponse {
    /**
     * Name of the file that needs to be pushed on the recipient
     */
    private StreamRecord streamRecord = null;

    public FileStreamResponse(BezirkZirkEndPoint sender, String sphereId, StreamRecord streamRecord){
        super(sender, streamRecord.getRecipientSEP(), sphereId);
        this.streamRecord = streamRecord;
    }

    public StreamRecord getStreamRecord() {
        return streamRecord;
    }

}
