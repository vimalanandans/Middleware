package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

public abstract class StreamAction extends ZirkAction {
    private final ZirkEndPoint recipient;
    private StreamDescriptor descriptor;
    private final short streamId;
    private boolean incremental;
    private boolean encrypted;
    private File file;
    private String streamActionName;

    public StreamAction(ZirkId zirkId, ZirkEndPoint recipient, /*StreamDescriptor descriptor,*/ short streamId, String streamActionName) {
        super(zirkId);

        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a streamDescriptor to a null recipient");
        }

        /*if (descriptor == null) {
            throw new IllegalArgumentException("Null or empty streamDescriptor specified when sending " +
                    "a file");
        }*/

        if(streamId < 0 ){
            throw new IllegalArgumentException("Invalid streamId specified streamId is" +streamId
                    +" stream id is Invalid" );
        }

        this.recipient = recipient;
        /*this.descriptor = descriptor;*/
        this.streamId = streamId;
        this.streamActionName = streamActionName;
    }

    public ZirkEndPoint getRecipient() {
        return recipient;
    }

    public StreamDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(StreamDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public short getStreamId() {
        return streamId;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getStreamActionName() {
        return streamActionName;
    }

    public void setStreamActionName(String streamActionName) {
        this.streamActionName = streamActionName;
    }
}
