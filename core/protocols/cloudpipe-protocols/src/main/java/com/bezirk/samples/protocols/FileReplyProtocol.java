package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.ProtocolRole;

public class FileReplyProtocol extends ProtocolRole {

    private String[] streamTopics = {new FileReply().topic};

    @Override
    public String getProtocolName() {
        return FileReplyProtocol.class.getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Test protocol representing stream descriptor for a file";
    }

    @Override
    public String[] getEventTopics() {
        return null;
    }

    @Override
    public String[] getStreamTopics() {
        return streamTopics == null ? null : streamTopics.clone();
    }

}
