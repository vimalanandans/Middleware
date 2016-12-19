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
package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.core.actions.StreamAction;

/**
 * Generic Streaming module interface.
 * A given type of Streaming will be instantiated in <code>ComponentManager</code>. every stream request from
 * Zirk {@link StreamAction} will be passed to Streaming implementation.
 *
 * Every incoming stream request will be passed to method {@link #addStreamRecordToQueue(StreamAction)}. In the implementation this {@link StreamAction}
 * will be converted to <code>StreamRecord</code> and stored in a ledger and a {@link StreamRequest} will be sent to the receiver {@link StreamAction#getStreamRequest().getRecipientEndPoint}
 *
 * The ledger will be of type <code>StreamBook</code> which will store all the <code>StreamRecord</code>
 *
 * A incremental file stream module implementation for Streaming interface is <code>FileStreaming</code>.
 */

public interface Streaming {

    /**
     * Interrupt a single streaming thread it could be either receiving thread or sender thread.
     */
    boolean interruptStream(final String streamKey);

    /**
     * Adds streaming request to StreamBook for processing.
     * <code>StreamBook</code> is a registry of all the stream requests.
     */
    boolean addStreamRecordToQueue(StreamAction streamAction);

}
