package test;

import com.bezirk.examples.protocols.parametricUI.NoticeUIshowText;
import com.bezirk.examples.protocols.parametricUI.RoleParametricUI;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.proxy.Factory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * @author Rishabh Gulati
 */
public class Pub {
    public static void main(String[] args) {
        Bezirk bezirk = Factory.registerZirk("Publisher zirk");

        bezirk.setLocation(new Location("null/null/kitchen"));
        /* subscribe to a protocol role */
        bezirk.subscribe(new RoleParametricUI(), new ReceiverListener());
        //display id of current service
        System.out.println("Publisher");

        /* set the targeted address */
        RecipientSelector target = new RecipientSelector(new Location("null/null/kitchen"));

        /* create the event to be published */
        NoticeUIshowText noticeUIshowText = new NoticeUIshowText("Hello", NoticeUIshowText.TextType.INFORMATION, 30000);

        /* publish the request to all in the target address */
        bezirk.sendEvent(target, noticeUIshowText);
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