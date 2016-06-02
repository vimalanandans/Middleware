/**
 * @author: Rishabh Gulati
 */
package test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message.Flag;
import com.bezirk.middleware.proxy.Factory;


/**
 * Written for the bezirk basics training
 */
public class Test {
    private Bezirk bezirk;

    public Test() {
        // set up sending messages over bezirk
        bezirk = Factory.getInstance();
        bezirk.registerZirk(Test.class.getSimpleName());
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.sayHello();
    }

    public void sayHello() {
        // Note: the usual way to say "hello world" in Java
        System.out.println("Hello World");

        // Steps to publish an even over Bezirk:
        // 1. set the event to be published
        Event hello = new Event(Flag.NOTICE, "Hello World");

        // 2. publish "hello world" to all in the target address
        bezirk.sendEvent(hello);

        // sanity check: display the event that was just published
        System.out.println("Published: " + hello.toJson());
    }
}
