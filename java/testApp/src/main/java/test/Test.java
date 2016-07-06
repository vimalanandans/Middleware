/**
 * @author: Rishabh Gulati
 */
package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.MulticastStream;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.proxy.Factory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

public class Test {
    private final Bezirk senderBezirk;
    private final Bezirk receiverBezirk;

    public Test() {
        senderBezirk = Factory.registerZirk("sender");
        receiverBezirk = Factory.registerZirk("receiver");

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

        Stream helloStream = new TestStream();
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
            String[] topics = {TestEvent.TOPIC};
            return topics;
        }

        public String[] getStreamTopics() {
            return null;
        }
    }

    private static class TestStream extends MulticastStream {
        public TestStream() {
            super(Flag.NOTICE, "Hello Stream", new RecipientSelector(new Location("test/location")));
        }
    }

    private static class ReceiverListener implements BezirkListener {
        @Override
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
            System.out.println("Received Event with topic: " + event.topic);
        }

        @Override
        public void receiveStream(String topic, Stream stream, short streamId, InputStream inputStream, ZirkEndPoint sender) {
            // TODO: Receive data stream
        }

        @Override
        public void receiveStream(String topic, Stream stream, short streamId, File file, ZirkEndPoint sender) {
            // TODO: Receive file stream
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }

        @Override
        public void pipeStatus(Pipe pipe, PipeStates status) {
        }

        @Override
        public void discovered(Set<DiscoveredZirk> zirkSet) {
        }

        @Override
        public void pipeGranted(Pipe pipe, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }
    }
}
