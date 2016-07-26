/**
 * @author: Rishabh Gulati
 */
package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.proxy.Factory;

import java.io.File;
import java.io.InputStream;

public class Test {
    private final Bezirk senderBezirk;

    public Test() {
        senderBezirk = Factory.registerZirk("sender");
        Bezirk receiverBezirk = Factory.registerZirk("receiver");

        receiverBezirk.subscribe(new TestRole(), new ReceiverListener());
        receiverBezirk.setLocation(new Location("test/location"));
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.send();
    }

    public void send() {
        Event helloEvent = new TestEvent();
        senderBezirk.sendEvent(helloEvent);
        System.out.println("Sender Published: " + helloEvent.toJson());

        StreamDescriptor helloStreamDescriptor = new TestStreamDescriptor();
        // TODO: create data stream and send it

        // TODO: send file stream
    }

    private static class TestEvent extends Event {
        private static final String TOPIC = "Hello World";

        public TestEvent() {
            super(Flag.NOTICE, TOPIC);
        }
    }

    private static class TestRole extends ProtocolRole {
        public String getRoleName() {
            return TestRole.class.getSimpleName();
        }

        public String getDescription() {
            return "This role is simply used for testing";
        }

        public String[] getEventTopics() {
            return new String[]{TestEvent.TOPIC};
        }

        public String[] getStreamTopics() {
            return null;
        }
    }

    private static class TestStreamDescriptor extends StreamDescriptor {
        public TestStreamDescriptor() {
            super(false, true);
        }
    }

    private static class ReceiverListener implements BezirkListener {
        @Override
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
            System.out.println("Received Event with topic: " + event.topic);
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream, ZirkEndPoint sender) {
            // TODO: Receive data streamDescriptor
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file, ZirkEndPoint sender) {
            // TODO: Receive file streamDescriptor
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }
    }
}
