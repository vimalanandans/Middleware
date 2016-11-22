package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.streaming.StreamReceiver;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.FileStreamResponse;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.bezirk.streaming.sender.FileStreamSenderThread;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamEventReceiver implements StreamReceiver{

    private StreamBook streamBook  = null;
    private FileStreamPortFactory portFactory = null;
    List<StreamEventObserver> streamEventObservers = null;

    public FileStreamEventReceiver(Comms comms){
        streamBook = new StreamBook();
        portFactory = new FileStreamPortFactory();

        //initialize the observers.
        streamEventObservers = new ArrayList<>();
        streamEventObservers.add(new StreamAliveObserver(comms));
        streamEventObservers.add(new StreamAssignedObserver());
    }

    private void initFileStreamReceiver(){
        streamBook.initStreamBook();
    }

    @Override
    public void incomingStreamRequest(StreamRequest streamRequest) {
        //when we receive the event.. notify subjects observers.
        for (StreamEventObserver streamObservers: streamEventObservers) {
            streamObservers.update(streamRequest, streamBook, portFactory);
        }
    }


}
