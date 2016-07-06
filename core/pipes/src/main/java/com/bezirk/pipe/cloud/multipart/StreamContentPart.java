package com.bezirk.pipe.cloud.multipart;

import java.io.InputStream;

public class StreamContentPart extends Part {

    // Expected header values for this part
    public static final String EXPECTEDVAL_CONTENT_TYPE = "application/octet-stream";
    public static final String EXPECTEDVAL_CONTENT_ENCODING = "binary";
    public static final String EXPECTEDVAL_CONTENT_ID = "content";

    // Location where the actual content starts in the inputStream
    private long offset = 0;

    public InputStream getContent() {
        return (InputStream) data;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}