package com.bezirk.test.streaming;

import com.bezirk.middleware.messages.StreamDescriptor;

import java.io.File;

/**
 * Created by pik6kor on 8/9/2016.
 */

public class StreamSend extends StreamDescriptor {

    public StreamSend(boolean isIncremental, boolean isEncrypted, File file) {
        super(isIncremental, isEncrypted, file);
    }

}