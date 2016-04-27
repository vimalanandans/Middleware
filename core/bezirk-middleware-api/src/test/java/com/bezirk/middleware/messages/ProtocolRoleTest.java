package com.bezirk.middleware.messages;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class ProtocolRoleTest {

    @Test
    public void test() {

        com.bezirk.middleware.messages.ProtocolRole dummyProtocolRole = new DummyProtocol();

        ProtocolRole streamlessProtocolRole = new StreamlessProtocol();

        assertFalse("Different protocol roles are considered equal.", dummyProtocolRole.equals(streamlessProtocolRole));
        assertNotEquals("Different protocol roles have same hashcode.", dummyProtocolRole.hashCode(), streamlessProtocolRole.hashCode());

        assertFalse("ProtocolRole is considered equal to test string..", dummyProtocolRole.equals("test"));

        ProtocolRole invalidProtocolRole = new InvalidProtocol();
        assertNotEquals("Different protocol roles have same hashcode.", invalidProtocolRole.hashCode(), streamlessProtocolRole.hashCode());

    }


    class DummyProtocol extends ProtocolRole {

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


    class StreamlessProtocol extends ProtocolRole {

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


    class InvalidProtocol extends ProtocolRole {

        private final String protocolName = null;

        private final String[] eventTopics = null;

        private final String[] streamTopics = null;

        public InvalidProtocol() {
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
