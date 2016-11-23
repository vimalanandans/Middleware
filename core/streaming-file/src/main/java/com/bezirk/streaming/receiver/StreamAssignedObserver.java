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

import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.bezirk.streaming.sender.FileStreamSenderThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * StreamAssignedObserver is a implementation of <code>StreamEventObserver</code>.
 * Update will be called when the subject StreamBook will be updated with a new entry.
 *
 * Created by PIK6KOR on 11/22/2016.
 */

class StreamAssignedObserver implements StreamEventObserver {

    //executor which handles the file stream receiving thread.
    private ExecutorService fileStreamSenderExecutor;

    //Thread size
    private static final int THREAD_SIZE = 10;

    StreamAssignedObserver(){
        this.fileStreamSenderExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
    }

    @Override
    public void update(StreamRequest streamRequest, StreamBook streamBook, FileStreamPortFactory portFactory) {

        FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;

        //update the status to addressed.
        StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.ASSIGNED == streamRecord.getStreamRecordStatus()){
            // This will be reply to the sender, Start the sender thread and initiate file transmission.
            FileStreamSenderThread fileStreamSenderThread = new FileStreamSenderThread(streamRecord);
            fileStreamSenderExecutor.execute(fileStreamSenderThread);

        }


    }
}
