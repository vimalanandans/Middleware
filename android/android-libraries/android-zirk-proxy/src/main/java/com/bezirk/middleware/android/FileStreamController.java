package com.bezirk.middleware.android;

import com.bezirk.middleware.streaming.Stream;
import com.bezirk.middleware.streaming.StreamController;

/**
 * This class provides the zirks to control the ongoing streaming.
 * StreamId wil be the primary key for handling streaming and this object with be returned once
 * {@link com.bezirk.middleware.Bezirk#sendStream(Stream)} has been initiated.
 *
 * Zirks can use {@link #stopStreaming()} method to interrupt the ongoing streaming.
 *
 */

class FileStreamController extends StreamController {

    private final String streamId;

    FileStreamController(String streamId){
        this.streamId = streamId;
    }


    @Override
    public void stopStreaming() {
        //TODO implement stopping streaming here..
        /*
        * Call this method from Zirk,
        * Implement here to give a call to streaming module and interrupt the stream sending or stream receiving thread instance.
        */
    }

    public String getStreamId() {
        return streamId;
    }

}
