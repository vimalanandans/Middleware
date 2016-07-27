/**
 * @author: Rishabh Gulati
 */
package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.proxy.Factory;

public class Test {
    private final Bezirk senderBezirk;

    public Test() {
        senderBezirk = Factory.registerZirk("sender");
        Bezirk receiverBezirk = Factory.registerZirk("receiver");

        EventSet es = new TestEventSet();

        es.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                System.out.println("Received Event with topic: " + event.topic);
            }
        });

        receiverBezirk.subscribe(es);
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

    private static class TestEventSet extends EventSet {
        public TestEventSet() {
            super(TestEvent.class);
        }
    }

    private static class TestStreamDescriptor extends StreamDescriptor {
        public TestStreamDescriptor() {
            super(false, true);
        }
    }
}
