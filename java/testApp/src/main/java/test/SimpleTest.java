package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.proxy.Factory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Temporary test till the 2 zirk registration problem is resolved. Used for subscribing to RoleParametricUI & receiving events on that role using gradle pub service from{@see https://github.com/Bezirk-Bosch/Tester}
 *
 * @author Rishabh Gulati
 */

public class SimpleTest {

    public static void main(String[] args) {
        Bezirk bezirk = Factory.registerZirk("Subscription zirk");

        bezirk.setLocation(new Location("null/null/kitchen"));
        /* subscribe to a protocol role */
        bezirk.subscribe(new ProtocolRoleTest(), new ReceiverListener());
        //display id of current service
        System.out.println("MY ID: " + UUID.randomUUID().toString().substring(0, 6));
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
