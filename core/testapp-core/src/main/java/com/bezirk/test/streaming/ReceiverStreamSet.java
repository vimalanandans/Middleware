package com.bezirk.test.streaming;

import com.bezirk.middleware.messages.StreamSet;

/**
 * Created by pik6kor on 8/9/2016.
 */

//StreamSet
class ReceiverStreamSet extends StreamSet {
    public ReceiverStreamSet(){
        super(StreamSend.class);
    }
}
