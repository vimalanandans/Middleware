package com.bezirk.pipe.core;

import com.bezirk.control.messages.pipes.PipeHeader;

import java.io.InputStream;

/**
 * Used to hold data needed to write content to disk
 */
public class WriteJob {

    protected boolean retainFile;

    protected InputStream inStream;

    protected String streamDescriptor;

    protected String shortFileName;

    protected PipeHeader pipeHeader;

    public String toString() {
        return "WriteJob: " + shortFileName + "," + streamDescriptor;
    }

    public boolean isRetainFile() {
        return retainFile;
    }

    public void setRetainFile(boolean retainFile) {
        this.retainFile = retainFile;
    }

    public InputStream getInputStream() {
        return inStream;
    }

    public void setInputStream(InputStream inStream) {
        this.inStream = inStream;
    }

    public String getStreamDescriptor() {
        return streamDescriptor;
    }

    public void setStreamDescriptor(String streamDescriptor) {
        this.streamDescriptor = streamDescriptor;
    }

    public String getShortFileName() {
        return shortFileName;
    }

    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
    }

    public PipeHeader getPipeHeader() {
        return pipeHeader;
    }

    public void setPipeHeader(PipeHeader pipeHeader) {
        this.pipeHeader = pipeHeader;
    }

}
