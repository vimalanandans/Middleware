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

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.streaming.StreamReceiver;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementaion of <code>StreamReceiver</code> all the receiving events will be received here and passed to observers
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamEventReceiver implements StreamReceiver{

    private StreamBook streamBook  = null;
    private FileStreamPortFactory portFactory = null;
    private List<StreamEventObserver> streamEventObservers = null;

    public FileStreamEventReceiver(Comms comms){
        streamBook = new StreamBook();
        portFactory = new FileStreamPortFactory();

        //initialize the observers.
        streamEventObservers = new ArrayList<>();
        streamEventObservers.add(new StreamAliveObserver(comms));
        streamEventObservers.add(new StreamAssignedObserver());
    }


    @Override
    public void incomingStreamRequest(StreamRequest streamRequest) {
        //when we receive the event.. notify subjects observers.
        for (StreamEventObserver streamObservers: streamEventObservers) {
            streamObservers.update(streamRequest, streamBook, portFactory);
        }
    }


}
