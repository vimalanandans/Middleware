package com.bezirk.control.messages.pipes;

import java.io.InputStream;

/**
 * Requirements:
 * <li> Represent multi and uni-cast messages
 * <li> Represent stream and regular response messages
 */
public class CloudStreamResponse extends CloudResponse {

    /**
     * Holds the stream content, or null if this is not a stream
     * message.
     */
    protected InputStream streamContent;

    /**
     * Supports download of partial stream content
     */
    protected long contentOffset = 0;

	/*
	 * Getters / setters
	 */

    public InputStream getStreamContent() {
        return streamContent;
    }

    public void setStreamContent(InputStream streamContent) {
        this.streamContent = streamContent;
    }

    public long getContentOffset() {
        return contentOffset;
    }

    public void setContentOffset(long contentOffset) {
        this.contentOffset = contentOffset;
    }

    public String getStreamDescriptor() {
        return super.getSerializedEvent();
    }

    public void setStreamDescriptor(String streamDescriptor) {
        super.setSerializedEvent(streamDescriptor);
    }

}
