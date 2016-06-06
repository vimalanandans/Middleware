/**
 * @author: Rishabh Gulati
 */
package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.Message.Flag;
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
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.sayHello();
    }

    public void sayHello() {
        // Steps to publish an even over Bezirk:
        // 1. set the event to be published
        Event hello = new Event(Flag.NOTICE, "Hello World");

        // 2. publish "hello world" to all in the target address
        senderBezirk.sendEvent(hello);

        // sanity check: display the event that was just published
        System.out.println("Sender Published: " + hello.toJson());
    }

    private static class ReceiverListener implements BezirkListener {
        @Override
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
            System.out.println(topic + " " + event.topic);
        }

        @Override
        public void receiveStream(String topic, Stream stream, short streamId, InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, Stream stream, short streamId, File file, ZirkEndPoint sender) {
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
