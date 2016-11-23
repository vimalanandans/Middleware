package com.bezirk.middleware.android;

import com.bezirk.middleware.streaming.StreamController;

/**
 * Class <code>FileStreamController<code/> extends <code>StreamController</code>will be return type for the sendStream in middleware-api project.
 * using this instance user can stop the streaming.
 *
 * Created by PIK6KOR on 11/23/2016.
 */

class FileStreamController extends StreamController {

    //primary key for streaming.
    private Short streamId;

    //constructor
    FileStreamController(Short streamId){
        this.streamId = streamId;
    }


    @Override
    public void stopStreaming(Short streamId) {
        //implement stopping streaming here..
    }

    public Short getStreamId() {
        return streamId;
    }

}
