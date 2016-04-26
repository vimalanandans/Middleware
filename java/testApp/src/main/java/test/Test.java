/**
 * @author: Rishabh Gulati
 */
package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message.Flag;
import com.bezirk.middleware.proxy.Factory;


/**
 * Written for the bezirk basics training
 */
public class Test {

    private Bezirk bezirk;
    private ZirkId myId;

    public Test() {
        // set up sending messages over bezirk
        bezirk = Factory.getInstance();
        myId = bezirk.registerZirk(Test.class.getSimpleName());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Test test = new Test();
        test.sayHello();

  /*      ZyreDemo zyreDemo = new ZyreDemo();
        zyreDemo.init();
        zyreDemo.onStart();*/
    }

    /**
     *
     */
    public void sayHello() {
        // Note: the usual way to say "hello world" in Java
        System.out.println("Hello World");

        // Steps to publish an even over UhU:
        // 1. set the targeted address
        Address target = new Address(new Location(null));        // local only (no pipes) with no constraints on location: will reach all services in the spheres test.Test is a member of

        // 2. set the event to be published
        Event hello = new Event(Flag.NOTICE, "Hello World");

        // 3. publish "hello world" to all in the target address
        bezirk.sendEvent(myId, target, hello);

        // sanity check: display the event that was just published
        System.out.println("Published: " + hello.toJson());
    }
}
