package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.Factory;

import java.io.File;
import java.io.InputStream;
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
        EventSet es = new EventSetTest();

        es.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                System.out.println("Received Event with topic: " + event.topic);
            }
        });

        bezirk.subscribe(es);
        //display id of current service
        System.out.println("MY ID: " + UUID.randomUUID().toString().substring(0, 6));
    }
}
