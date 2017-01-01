package com.bezirk.middleware.android;

import com.bezirk.middleware.streaming.StreamController;

/**
 * Class {@link FileStreamController} extends {@link StreamController} is class provides action to control File Streaming.
 * using this instance user can stop the streaming.
 *
 */

class FileStreamController extends StreamController {

    //primary key for streaming.
    private final Long streamId;

    //constructor
    FileStreamController(Long streamId){
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

    public Long getStreamId() {
        return streamId;
    }

}
