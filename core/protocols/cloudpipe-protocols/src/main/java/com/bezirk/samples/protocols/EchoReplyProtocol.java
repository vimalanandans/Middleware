package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.ProtocolRole;

public class EchoReplyProtocol extends ProtocolRole {
    private String protocolName = EchoReplyProtocol.class.getSimpleName();

    private String[] eventTopics = {new EchoReply().topic};

    private String[] streamTopics = {};

    @Override
    public String getProtocolName() {
        return protocolName;
    }

    @Override
    public String getDescription() {
        return "Protocol containing echo reply messages";
    }

    @Override
    public String[] getEventTopics() {
        return eventTopics == null ? null : eventTopics.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return streamTopics == null ? null : streamTopics.clone();
    }

}
