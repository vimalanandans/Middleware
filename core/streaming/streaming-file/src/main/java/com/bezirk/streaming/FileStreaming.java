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

import com.bezirk.middleware.core.actions.StreamAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.streaming.FileStreamRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PIK6KOR on 11/1/2016.
 */

public final class FileStreaming implements Streaming {

    //inject the object of comms.
    private Comms comms;

    //Streaming Request queue.
    private final Map<Short, StreamRecord> streamingQueue = new HashMap<Short, StreamRecord>();

    //Gson
    private final Gson gson = new Gson();

    public FileStreaming(Comms comms){
        this.comms = comms;
    }

    @Override
    public boolean interruptStream(String streamKey) {
        return false;
    }

    @Override
    public boolean addStreamRecordToQueue(StreamAction streamAction) {
        //prepare stream record from streamAction and save this in the map.
        StreamRecord streamRecord = null;

        if(streamAction.getStreamRequest() instanceof FileStreamRequest){
            FileStreamRequest fileStreamRequest = (FileStreamRequest) streamAction.getStreamRequest();
            streamRecord = new StreamRecord(streamAction.getStreamId(), fileStreamRequest.getRecipientEndPoint(), fileStreamRequest.getFile());
        }

        //add the record to streaming
        boolean isAdded = addStreamRecordToMap(streamRecord);

        //send a event to receiver when the sr=treamRecord is stored
        if(isAdded){
            ControlLedger controlLedger = new ControlLedger();

            //sphere will be dummy as of now
            controlLedger.setSphereId("DEFAULT");

            StreamRequest streamRequest = new StreamRequest();

            controlLedger.setMessage(streamRequest);
            controlLedger.setSerializedMessage(gson.toJson(streamRequest));

            comms.sendControlLedger(controlLedger);
        }else{

        }
        return false;
    }

    /**
     * add Stream Record to Stream Sending queue.
     * @param sRecord
     * @return
     */
    private final boolean addStreamRecordToMap(StreamRecord sRecord) {
        synchronized (this) {
            if (streamingQueue.containsKey(sRecord.getStreamId())) {
                return false;
            } else {
                streamingQueue.put(sRecord.getStreamId(), sRecord);
                return true;
            }
        }
    }

    /**
     * check for duplicate
     * @param streamKey
     * @return
     */
    private final boolean checkStreamRequestForDuplicate(
            String streamKey) {
        return streamingQueue.containsKey(streamKey);
    }
}
