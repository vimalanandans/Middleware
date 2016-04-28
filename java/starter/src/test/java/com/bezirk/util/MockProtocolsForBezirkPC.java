package com.bezirk.util;

import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;

/**
 * This class provides different protocols which are used in the testcases.
 * <p/>
 * a) EventlessProtocol  - ProtocolRole with empty event topics.
 * b) StreamlessProtocol - ProtocolRole with empty stream topics.
 * c) DummyProtocol      - ProtocolRole with non empty event and stream topics.
 *
 * @author AJC6KOR
 */
public class MockProtocolsForBezirkPC {


    public class MockEvent1 extends Event {

        private String reply = "Yes. I got your message";

        public MockEvent1(Flag flag, String topic) {
            super(flag, topic);
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }


    }


    public class StreamlessProtocol extends ProtocolRole {

        private static final String protocolName = "StreamlessProtocol";

        private final String[] eventTopics = new String[]{"MockEvent1", "MockEvent2"};

        private final String[] streamTopics = null;

        public StreamlessProtocol() {
            super();
        }

        @Override
        public String getProtocolName() {
            return protocolName;
        }

        @Override
        public String getDescription() {
            return protocolName;
        }

        @Override
        public String[] getEventTopics() {
            return eventTopics;
        }

        @Override
        public String[] getStreamTopics() {
            return streamTopics;
        }


    }


    public class EventlessProtocol extends ProtocolRole {

        private static final String protocolName = "EventlessProtocol";

        private final String[] eventTopics = null;

        private final String[] streamTopics = new String[]{"DummyStream1", "DummyStream2"};

        public EventlessProtocol() {
            super();
        }

        @Override
        public String getProtocolName() {
            return protocolName;
        }

        @Override
        public String getDescription() {
            return protocolName;
        }

        @Override
        public String[] getEventTopics() {
            return eventTopics;
        }

        @Override
        public String[] getStreamTopics() {
            return streamTopics;
        }


    }


    public class DummyProtocol extends ProtocolRole {

        private static final String protocolName = "DummyProtocol";

        private final String[] eventTopics = new String[]{"MockEvent1", "MockEvent2"};

        private final String[] streamTopics = new String[]{"DummyStream1", "DummyStream2"};

        public DummyProtocol() {
            super();
        }

        @Override
        public String getProtocolName() {
            return protocolName;
        }

        @Override
        public String getDescription() {
            return protocolName;
        }

        @Override
        public String[] getEventTopics() {
            return eventTopics;
        }

        @Override
        public String[] getStreamTopics() {
            return streamTopics;
        }


    }

}