package com.bezirk.streaming.sender;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.util.ValidatorUtility;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by PIK6KOR on 11/11/2016.
 */

public class StreamQueueProcessor implements Runnable {
    @Override
    public void run() {

    }

    /*public StreamQueueProcessor(){

    }

    @Override
    public void run() {
        boolean running = true;
        boolean bezirkCallbackPresent = false;
        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                running = false;
                continue;
            }

            //using CopyOnWriteArrayList for sync write and reads.
            List<ControlMessage> streamQueue = new CopyOnWriteArrayList<>(
                    msgQueue.getQueue()); // pop the StreamDescriptor record
            Iterator<ControlMessage> it = streamQueue.iterator();

            while (it.hasNext()) {
                StreamRecord streamRecord = (StreamRecord) it.next();

                if (StreamRecordStatus.LOCAL == streamRecord.getStreamRecordStatus()) {
                    processLocalStreamMessage(bezirkCallbackPresent, streamRecord);

                } else if (StreamRecordStatus.ADDRESSED == streamRecord.getStreamRecordStatus()) {
                    logger.debug("StreamDescriptor Request is already Addressed.");
                } else if (streamRecord.getStreamRecordStatus()== StreamRecordStatus.READY) {
                    processStreamReadyMessage(streamRecord);
                } else if (streamRecord.getStreamRecordStatus() == StreamRecordStatus.BUSY) {
                    processStreamBusyMessage(bezirkCallbackPresent, streamRecord);
                }
                msgQueue.removeFromQueue(streamRecord);

                //punith.. remove from active stream map
            }
        }
    }*/
}
