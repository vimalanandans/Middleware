package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.messages.StreamDescriptor;

import java.io.File;

/**
 * Created by pik6kor on 8/9/2016.
 */

public class StreamSend extends StreamDescriptor {
    /*//File which has to be streamed!!
    private File file;*/

    public StreamSend(boolean isIncremental, boolean isEncrypted, File file) {
        super(isIncremental, isEncrypted, file, StreamSend.class.getCanonicalName());
    }
}