package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;

/**
 * Created by PIK6KOR on 11/22/2016.
 */

public interface StreamEventObserver {

    public void update(StreamRequest streamRequest, StreamBook streamBook, FileStreamPortFactory portFactory);
}
