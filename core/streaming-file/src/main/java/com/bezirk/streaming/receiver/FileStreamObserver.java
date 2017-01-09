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
package com.bezirk.streaming.receiver;

import java.util.Observer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.GenerateMsgId;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamRecord;
import com.google.gson.Gson;

/**
 * All observers for file streaming status update will extend this class.
 * This provides a common feature to reply a controlMessage to sender about the stream status update.
 */

abstract class FileStreamObserver implements Observer  {
    private static final Logger logger = LoggerFactory.getLogger(FileStreamObserver.class);
    private static final String SPHERE_ID = "DEFAULT";
    private final Gson gson = new Gson();

    /**
     * After updating the #StreamBook, Send a control message to sender with updated #StreamRecord status.
     * @param streamRecord streamRecord.
     */
    void replyToSender(@NotNull StreamRecord streamRecord, @NotNull Comms comms) {
        final ControlLedger controlLedger = new ControlLedger();
        controlLedger.setSphereId(SPHERE_ID);

        final UnicastHeader uHeader = new UnicastHeader();
        uHeader.setRecipient(streamRecord.getSenderServiceEndPoint());
        uHeader.setSender(streamRecord.getRecipientServiceEndPoint());
        uHeader.setUniqueMsgId(GenerateMsgId.generateEvtId(streamRecord.getRecipientServiceEndPoint()));
        controlLedger.setHeader(uHeader);

        final BezirkZirkEndPoint sender = streamRecord.getSenderServiceEndPoint();
        final BezirkZirkEndPoint recipient = streamRecord.getRecipientServiceEndPoint();
        streamRecord.setSenderServiceEndPoint(recipient);
        streamRecord.setRecipientServiceEndPoint(sender);

        final FileStreamRequest streamResponse = new FileStreamRequest(streamRecord.getSenderServiceEndPoint(), SPHERE_ID, streamRecord);
        controlLedger.setMessage(streamResponse);
        controlLedger.setSerializedMessage(gson.toJson(streamResponse));

        logger.debug("sending a reply to sender for stream request {}", streamRecord.getStreamId());
        comms.sendControlLedger(controlLedger);
    }
}
