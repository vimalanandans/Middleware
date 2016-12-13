package com.bezirk.middleware.android;

import com.bezirk.middleware.streaming.StreamController;

/**
 * Class {@link FileStreamController} extends {@link StreamController} is class provides action to control File Streaming.
 * using this instance user can stop the streaming.
 *
 */

class FileStreamController extends StreamController {

    //primary key for streaming.
    private Short streamId;

    //constructor
    FileStreamController(Short streamId){
        this.streamId = streamId;
    }


    @Override
    public void stopStreaming() {
        //implement stopping streaming here..
    }

    public Short getStreamId() {
        return streamId;
    }

}
