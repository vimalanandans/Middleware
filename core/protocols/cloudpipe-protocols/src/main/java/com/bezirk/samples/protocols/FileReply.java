package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.Stream;

public class FileReply extends Stream {

    public static final String TOPIC = FileReply.class.getSimpleName();

    private String fileName = null;

    public FileReply() {
        super(Flag.REPLY, TOPIC);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
